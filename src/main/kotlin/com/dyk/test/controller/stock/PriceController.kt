package com.dyk.test.controller.stock

import com.dyk.test.dto.PriceResponse
import com.dyk.test.service.stock.PriceService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
class PriceController(
    private val priceService: PriceService
) {
    @GetMapping("/api/adjClose")
    fun getAdjClose(
        @RequestParam ticker: String,
        @RequestParam startTime: String,
        @RequestParam endTime: String
    ): List<PriceResponse> {
        val startDateTime = LocalDateTime.parse(startTime)
        val endDateTime = LocalDateTime.parse(endTime)
        return priceService.getAdjCloseForTickerInRange(ticker, startDateTime, endDateTime)
    }
}