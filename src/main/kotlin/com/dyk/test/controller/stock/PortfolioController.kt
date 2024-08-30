package com.dyk.test.controller.stock

import com.dyk.test.config.security.jwt.JwtAuth
import com.dyk.test.dto.CreatePortfolioRequest
import com.dyk.test.dto.PortfolioDto
import com.dyk.test.dto.StockRequest
import com.dyk.test.service.stock.PortfolioService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/portfolio")
class PortfolioController(
    private val portfolioService: PortfolioService
) {

    @JwtAuth
    @PostMapping
    fun createPortfolio(@RequestBody request: CreatePortfolioRequest): ResponseEntity<out Any> {
        val authentication = SecurityContextHolder.getContext().authentication
        request.username = authentication.name
        try {
            val portfolio = portfolioService.createPortfolio(request)
            return ResponseEntity(portfolio, HttpStatus.CREATED)
        } catch (e: IllegalStateException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to (e.message ?: "Invalid create")))
        }
    }

    @JwtAuth
    @PostMapping("/{portfolioId}/stocks")
    fun addStocksToPortfolio(
        @PathVariable portfolioId: UUID,
        @RequestBody stockRequests: List<StockRequest>
    ): ResponseEntity.BodyBuilder {
        portfolioService.addStocksToPortfolio(portfolioId, stockRequests)
        return ResponseEntity.ok()
    }

    @JwtAuth
    @GetMapping("/{portfolioId}")
    fun getPortfolio(
        @PathVariable portfolioId: UUID
    ): ResponseEntity<PortfolioDto> {
        val portfolio = portfolioService.getPortfolioDetails(portfolioId)
        return ResponseEntity(portfolio, HttpStatus.OK)
    }
}