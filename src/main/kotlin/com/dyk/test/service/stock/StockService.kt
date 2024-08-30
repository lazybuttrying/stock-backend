package com.dyk.test.service.stock

import com.dyk.test.dto.StockDTO
import com.dyk.test.repository.stock.StockRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service


@Service
class StockService(private val stockRepository: StockRepository) {

    fun findSimilarStocks(keyword: String, page: Int, size:Int): Map<String, Any> {
        val pageable = PageRequest.of(page, size)
        val allStocks = stockRepository.findByContentContainingIgnoreCase(keyword, pageable)
        val similarStocks = allStocks.filter {
            stock -> levenshteinDistance(stock.name, keyword) <= 3
        }
        val pageResult = similarStocks.chunked(size)
        val currentPageStocks = pageResult.getOrElse(page) { emptyList() }
        return mapOf(
            "content" to currentPageStocks.map {StockDTO(ticker = it.ticker, name = it.name)},
            "totalPages" to pageResult.size,
            "currentPage" to page,
            "totalElements" to similarStocks.size
        )
    }

    private fun levenshteinDistance(s1: String, s2: String): Int {
        val dist = Array(s1.length + 1) { IntArray(s2.length + 1) }
        for (i in s1.indices) dist[i][0] = i
        for (j in s2.indices) dist[0][j] = j
        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dist[i][j] = minOf(dist[i - 1][j] + 1, dist[i][j - 1] + 1, dist[i - 1][j - 1] + cost)
            }
        }
        return dist[s1.length][s2.length]
    }
}