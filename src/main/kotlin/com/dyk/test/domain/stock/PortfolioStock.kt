package com.dyk.test.domain.stock


import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "portfolio_stock")
data class PortfolioStock(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    val id: UUID?= null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    val portfolio: Portfolio,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_ticker", nullable = false)
    val stock: Stock,

    @Column(nullable = false)
    val count: Int,

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @UpdateTimestamp
    val updatedAt: LocalDateTime? = null
)