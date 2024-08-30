package com.dyk.test.controller.stock

import com.dyk.test.service.stock.StockService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/stock")
class StockController(private val stockService: StockService) {

    @GetMapping("/search")
    fun getSimilarStocks(
        @RequestParam keyword: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): Map<String, Any> {
        return stockService.findSimilarStocks(keyword, page, size)
    }
}