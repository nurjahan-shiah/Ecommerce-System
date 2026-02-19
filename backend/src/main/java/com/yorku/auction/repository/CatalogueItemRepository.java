package com.yorku.auction.repository;

import com.yorku.auction.model.CatalogueItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CatalogueItemRepository extends JpaRepository<CatalogueItem, Long> {

    @Query("""
        SELECT c FROM CatalogueItem c
        WHERE lower(c.item_name) LIKE lower(concat('%', :q, '%'))
           OR lower(c.keywords) LIKE lower(concat('%', :q, '%'))
    """)
    List<CatalogueItem> search(@Param("q") String q);
}
