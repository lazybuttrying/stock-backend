package com.dyk.test.repository.stock

import com.dyk.test.domain.stock.Price
import com.dyk.test.domain.stock.PriceId
import com.dyk.test.domain.stock.Stock
import com.dyk.test.dto.PriceResponse
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface PriceRepository : JpaRepository<Price, PriceId> {
    fun findTopByStockOrderByTimeDesc(stock: Stock): Price?

    @Query("SELECT new com.dyk.test.dto.PriceResponse(p.time, p.adjClose) FROM Price p WHERE p.ticker = :ticker AND p.time BETWEEN :startTime AND :endTime")
    fun findAdjCloseByTickerAndTimeRange(
        @Param("ticker") ticker: String,
        @Param("startTime") startTime: LocalDateTime,
        @Param("endTime") endTime: LocalDateTime
    ): List<PriceResponse>
}