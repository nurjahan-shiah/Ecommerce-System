package com.yorku.auction.controller;

import com.yorku.auction.dto.CatalogueItemResponse;
import com.yorku.auction.service.CatalogueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogue")
@CrossOrigin(origins="*")
public class CatalogueController {

    private final CatalogueService catalogueService;

    public CatalogueController(CatalogueService catalogueService) {
        this.catalogueService = catalogueService;
    }

    // UC2.2 initial load
    @GetMapping("/items/active")
    public ResponseEntity<List<CatalogueItemResponse>> active() {
        return ResponseEntity.ok(catalogueService.getActiveAuctions(null));
    }

    // UC2.1 search
    @GetMapping("/items")
    public ResponseEntity<List<CatalogueItemResponse>> search(@RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(catalogueService.getActiveAuctions(keyword));
    }
}