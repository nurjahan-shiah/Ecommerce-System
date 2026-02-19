package com.yorku.auction.controller;

import com.yorku.auction.model.CatalogueItem;
import com.yorku.auction.repository.CatalogueItemRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogue")
@CrossOrigin(origins = "*")
public class CatalogueController {

    private final CatalogueItemRepository repo;

    public CatalogueController(CatalogueItemRepository repo) {
        this.repo = repo;
    }

    // UC2.1: Item Search by keyword
    @GetMapping("/search")
    public List<CatalogueItem> search(@RequestParam("q") String q) {
        if (q == null || q.trim().isEmpty()) return List.of();
        return repo.search(q.trim());
    }

    // Optional: list all items (debug/testing)
    @GetMapping
    public List<CatalogueItem> all() {
        return repo.findAll();
    }
}
