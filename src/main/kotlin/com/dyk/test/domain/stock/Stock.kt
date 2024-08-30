package com.dyk.test.domain.stock

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "stock")
data class Stock(
    @Id
    val ticker: String,

    val name: String,

    val market: String,

    val industry: String,

    val sector: String,

    @OneToMany(mappedBy = "stock", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val prices: List<Price> = emptyList(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id")
    val portfolio: Portfolio? = null,

    @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinTable(
        name = "stock_news",
        joinColumns = [JoinColumn(name = "stock_ticker")],
        inverseJoinColumns = [JoinColumn(name = "news_id")]
    )
    val news: List<News> = emptyList(),

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @UpdateTimestamp
    var updatedAt: LocalDateTime? = null,

    var deletedAt: LocalDateTime? = null
)