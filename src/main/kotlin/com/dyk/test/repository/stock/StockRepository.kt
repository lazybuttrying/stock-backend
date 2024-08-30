package com.dyk.test.repository.stock

import com.dyk.test.domain.stock.Stock
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param


interface StockRepository : JpaRepository<Stock, String> {
    @Query("SELECT a FROM Stock a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    fun findByContentContainingIgnoreCase(@Param("name") name: String, pageable: Pageable): List<Stock>
}
