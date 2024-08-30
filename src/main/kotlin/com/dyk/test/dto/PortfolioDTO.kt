package com.dyk.test.dto

import java.util.*


data class CreatePortfolioRequest(
    val title: String,
    val description: String = "",
    var username: String = ""
)

data class StockRequest(
    val ticker: String,
    val count: Int
)

data class PortfolioStockDto(
    val ticker: String,
    val name: String,
    val count: Int,
    val latestAdjClosePrice: Float
)

data class PortfolioDto(
    val portfolioId: UUID,
    val title: String,
    val description: String = "",
    val stocks: List<PortfolioStockDto>
)