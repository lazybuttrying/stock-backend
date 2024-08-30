package com.dyk.test.service.stock

import com.dyk.test.dto.PriceResponse
import com.dyk.test.repository.stock.PriceRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime


@Service
class PriceService(
    private val priceRepository: PriceRepository
) {
    fun getAdjCloseForTickerInRange(ticker: String, startTime: LocalDateTime, endTime: LocalDateTime): List<PriceResponse> {
        return priceRepository.findAdjCloseByTickerAndTimeRange(ticker, startTime, endTime)
    }
}