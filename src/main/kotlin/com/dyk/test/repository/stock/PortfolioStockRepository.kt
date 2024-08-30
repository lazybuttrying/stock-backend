package com.dyk.test.repository.stock

import com.dyk.test.domain.stock.Portfolio
import com.dyk.test.domain.stock.PortfolioStock
import org.springframework.data.jpa.repository.JpaRepository


interface PortfolioStockRepository : JpaRepository<PortfolioStock, Long> {
    fun findByPortfolio(portfolio: Portfolio): List<PortfolioStock>
}