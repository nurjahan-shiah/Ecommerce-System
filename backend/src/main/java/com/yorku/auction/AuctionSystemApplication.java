package com.yorku.auction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot Application for EECS 4413 Auction System
 * Team Atlas - Forward Auction E-Commerce System
 */
@SpringBootApplication
public class AuctionSystemApplication {

    public static void main(String[] args) {
    	
        SpringApplication.run(AuctionSystemApplication.class, args);
        System.out.println("========================================");
        System.out.println("Auction System Started Successfully!");
        System.out.println("Access at: http://localhost:8080");
        System.out.println("========================================");
        
    }
}
