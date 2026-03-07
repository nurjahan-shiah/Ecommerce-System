#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080/api}"
NOW_TS="$(date +%s)"

BUYER_EMAIL="bob_${NOW_TS}@example.com"
BUYER_USERNAME="bob_${NOW_TS}"
SELLER_EMAIL="seller_${NOW_TS}@example.com"
SELLER_USERNAME="seller_${NOW_TS}"

WINNER_EMAIL="${WINNER_EMAIL:-testuser4@gmail.com}"
WINNER_PASSWORD="${WINNER_PASSWORD:-password123}"
WINNER_AUCTION_ID="${WINNER_AUCTION_ID:-5}"

print_section() {
  echo
  echo "============================================================"
  echo "MAIN USE CASE: $1"
  echo "============================================================"
}

extract_json_number() {
  local body="$1"
  local key="$2"
  echo "$body" | tr -d '\n' | grep -o "\"${key}\"[[:space:]]*:[[:space:]]*[0-9][0-9]*" | head -n1 | sed -E "s/.*:[[:space:]]*([0-9]+)$/\1/"
}

extract_json_string() {
  local body="$1"
  local key="$2"
  echo "$body" | tr -d '\n' | grep -o "\"${key}\"[[:space:]]*:[[:space:]]*\"[^\"]*\"" | head -n1 | sed -E "s/.*:[[:space:]]*\"([^\"]*)\"$/\1/"
}

request() {
  local method="$1"
  local url="$2"
  local data="${3:-}"
  local headers=("${@:4}")

  local curl_args=(
    -sS
    -X "$method"
    -H "Content-Type: application/json"
  )

  for h in "${headers[@]}"; do
    curl_args+=(-H "$h")
  done

  if [[ -n "$data" ]]; then
    curl_args+=(-d "$data")
  fi

  local response
  response=$(curl "${curl_args[@]}" "$url" -w $'\n%{http_code}')
  local status body
  status="$(echo "$response" | tail -n1)"
  body="$(echo "$response" | sed '$d')"

  echo "$status"
  echo "$body"
}

assert_status() {
  local status="$1"
  local allowed="$2"
  local context="$3"
  if ! echo "$allowed" | tr ',' '\n' | grep -qx "$status"; then
    echo "ERROR: $context failed (HTTP $status, expected: $allowed)"
    exit 1
  fi
}

echo "Using API base URL: $BASE_URL"

print_section "UC1 - USER SIGN-UP AND LOGIN"

readarray -t signup_out < <(request POST "$BASE_URL/auth/signup" "{
  \"username\": \"$BUYER_USERNAME\",
  \"password\": \"password123\",
  \"role\": \"BUYER\",
  \"email\": \"$BUYER_EMAIL\",
  \"first_name\": \"Bob\",
  \"last_name\": \"Mainflow\",
  \"street_number\": \"101\",
  \"street_name\": \"Main St\",
  \"city\": \"Toronto\",
  \"country\": \"Canada\",
  \"postal_code\": \"M1M1M1\"
}")
assert_status "${signup_out[0]}" "200,201" "UC1 sign-up"
echo "Sign-up response: ${signup_out[1]}"

readarray -t login_out < <(request POST "$BASE_URL/auth/login" "{
  \"email\": \"$BUYER_EMAIL\",
  \"password\": \"password123\"
}")
assert_status "${login_out[0]}" "200" "UC1 login"
BUYER_SESSION_ID="$(extract_json_string "${login_out[1]}" "sessionId")"
BUYER_USER_ID="$(extract_json_number "${login_out[1]}" "userId")"
echo "Login OK. sessionId=$BUYER_SESSION_ID userId=$BUYER_USER_ID"

print_section "UC7 - SELLER UPLOADS A NEW AUCTION ITEM"

readarray -t seller_signup_out < <(request POST "$BASE_URL/auth/signup" "{
  \"username\": \"$SELLER_USERNAME\",
  \"password\": \"password123\",
  \"role\": \"SELLER\",
  \"email\": \"$SELLER_EMAIL\",
  \"first_name\": \"Seller\",
  \"last_name\": \"Mainflow\",
  \"street_number\": \"202\",
  \"street_name\": \"Seller Ave\",
  \"city\": \"Toronto\",
  \"country\": \"Canada\",
  \"postal_code\": \"M2M2M2\"
}")
assert_status "${seller_signup_out[0]}" "200,201" "Seller sign-up"

readarray -t seller_login_out < <(request POST "$BASE_URL/auth/login" "{
  \"email\": \"$SELLER_EMAIL\",
  \"password\": \"password123\"
}")
assert_status "${seller_login_out[0]}" "200" "Seller login"
SELLER_USER_ID="$(extract_json_number "${seller_login_out[1]}" "userId")"
echo "Seller login OK. userId=$SELLER_USER_ID"

readarray -t add_item_out < <(request POST "$BASE_URL/seller/auctions" "{
  \"itemName\": \"Main Flow Demo Item $NOW_TS\",
  \"description\": \"Uploaded by seller in the main flow script\",
  \"startingPrice\": 50.0,
  \"durationHours\": 24,
  \"keywords\": \"mainflow,demo,item\",
  \"shippingPrice\": 10.0,
  \"expeditedShippingPrice\": 5.0,
  \"shippingDays\": 3
}" "X-User-Id: $SELLER_USER_ID")
assert_status "${add_item_out[0]}" "200" "UC7 add item"
NEW_AUCTION_ID="$(extract_json_number "${add_item_out[1]}" "auctionId")"
echo "Auction created. auctionId=$NEW_AUCTION_ID"

print_section "UC2 - BROWSE CATALOGUE AND SELECT ITEM IN SESSION"

readarray -t search_out < <(request GET "$BASE_URL/catalogue/items?keyword=mainflow")
assert_status "${search_out[0]}" "200" "UC2 keyword search"
echo "Search completed for keyword 'mainflow'."

readarray -t select_out < <(request POST "$BASE_URL/session/selection" "{
  \"auctionId\": $NEW_AUCTION_ID
}" "X-Session-Id: $BUYER_SESSION_ID" "X-User-Id: $BUYER_USER_ID")
assert_status "${select_out[0]}" "200" "UC2 select item"
echo "Selection response: ${select_out[1]}"

print_section "UC3 - PLACE A VALID HIGHER BID"

readarray -t bid_out < <(request POST "$BASE_URL/bids" "{
  \"bidAmount\": 60.0
}" "X-Session-Id: $BUYER_SESSION_ID" "X-User-Id: $BUYER_USER_ID")
assert_status "${bid_out[0]}" "200" "UC3 place bid"
echo "Bid response: ${bid_out[1]}"

print_section "UC5/UC6 - PAYMENT COMPLETION + RECEIPT/SHIPMENT INFO"
echo "Using seeded winner flow: $WINNER_EMAIL on auctionId=$WINNER_AUCTION_ID"

readarray -t winner_login_out < <(request POST "$BASE_URL/auth/login" "{
  \"email\": \"$WINNER_EMAIL\",
  \"password\": \"$WINNER_PASSWORD\"
}")
assert_status "${winner_login_out[0]}" "200" "Winner login"
WINNER_SESSION_ID="$(extract_json_string "${winner_login_out[1]}" "sessionId")"
WINNER_USER_ID="$(extract_json_number "${winner_login_out[1]}" "userId")"

readarray -t winner_select_out < <(request POST "$BASE_URL/session/selection" "{
  \"auctionId\": $WINNER_AUCTION_ID
}" "X-Session-Id: $WINNER_SESSION_ID" "X-User-Id: $WINNER_USER_ID")
assert_status "${winner_select_out[0]}" "200" "Winner select auction"

readarray -t payment_out < <(request POST "$BASE_URL/payments/pay" "{
  \"auctionId\": $WINNER_AUCTION_ID,
  \"expeditedShipping\": true,
  \"cardNumber\": \"4111111111111111\",
  \"cardName\": \"Main Flow Winner\",
  \"expiryDate\": \"12/27\",
  \"securityCode\": \"123\"
}" "X-Session-Id: $WINNER_SESSION_ID" "X-User-Id: $WINNER_USER_ID")
assert_status "${payment_out[0]}" "200" "UC5 payment"
echo "Payment response (includes receipt/shipment fields): ${payment_out[1]}"

echo
echo "Main flow script finished successfully."
