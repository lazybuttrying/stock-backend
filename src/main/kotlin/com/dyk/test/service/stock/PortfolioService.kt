package com.dyk.test.service.stock

import com.dyk.test.domain.stock.Portfolio
import com.dyk.test.domain.stock.PortfolioStock
import com.dyk.test.dto.CreatePortfolioRequest
import com.dyk.test.dto.PortfolioDto
import com.dyk.test.dto.PortfolioStockDto
import com.dyk.test.dto.StockRequest
import com.dyk.test.repository.stock.PortfolioRepository
import com.dyk.test.repository.stock.PortfolioStockRepository
import com.dyk.test.repository.stock.PriceRepository
import com.dyk.test.repository.stock.StockRepository
import com.dyk.test.repository.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class PortfolioService(
    private val userRepository: UserRepository,
    private val portfolioRepository: PortfolioRepository,
    private val stockRepository: StockRepository,
    private val portfolioStockRepository: PortfolioStockRepository,
    private val priceRepository: PriceRepository
) {
    @Transactional
    fun addStocksToPortfolio(portfolioId: UUID, stockRequests: List<StockRequest>) {
        val portfolio = portfolioRepository.findById(portfolioId)
            .orElseThrow { IllegalArgumentException("Portfolio not found with id: $portfolioId") }

        stockRequests.forEach { stockRequest ->
            val stock = stockRepository.findById(stockRequest.ticker)
                .orElseThrow { IllegalArgumentException("Stock not found with ticker: ${stockRequest.ticker}") }

            val portfolioStock = PortfolioStock(
                portfolio = portfolio,
                stock = stock,
                count = stockRequest.count
            )

            portfolioStockRepository.save(portfolioStock)
        }
    }

    @Transactional
    fun createPortfolio(request: CreatePortfolioRequest): Portfolio {
        val user = userRepository.findByUsername(request.username)
            ?: throw IllegalArgumentException("User not found with id: ${request.username}")

        val portfolio = Portfolio(
            title = request.title,
            description = request.description,
            user = user
        )
        return portfolioRepository.save(portfolio)
    }

    @Transactional(readOnly = true)
    fun getPortfolioDetails(portfolioId: UUID): PortfolioDto? {
        val portfolio = portfolioRepository.findById(portfolioId)
            .orElseThrow { IllegalArgumentException("Portfolio not found with id: $portfolioId") }

        val portfolioStocks = portfolioStockRepository.findByPortfolio(portfolio)

        val stockDtos = portfolioStocks.map { portfolioStock ->
            val stock = portfolioStock.stock
            val latestPrice = priceRepository.findTopByStockOrderByTimeDesc(stock)
                ?.adjClose ?: 0.0

            PortfolioStockDto(
                ticker = stock.ticker,
                name = stock.name,
                count = portfolioStock.count,
                latestAdjClosePrice = latestPrice.toFloat()
            )
        }

        return portfolio.id?.let {
            PortfolioDto(
                portfolioId = it,
                title = portfolio.title,
                description = portfolio.description,
                stocks = stockDtos
            )
        }
    }
}

