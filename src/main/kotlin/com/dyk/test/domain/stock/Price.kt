package com.dyk.test.domain.stock

import jakarta.annotation.Nullable
import jakarta.persistence.*
import java.time.LocalDateTime
import org.hibernate.annotations.CreationTimestamp

@Entity
@Table(name = "price")
@IdClass(PriceId::class)
data class Price(
    @Id
    val ticker: String,              // 종목 코드

    @Id
    val time: LocalDateTime,         // 타임스탬프

    @Nullable
    val open: Float,

    @Nullable
    val high: Float,

    @Nullable
    val low: Float,

    @Nullable
    val close: Float,

    @Nullable
    val adjClose: Float,

    @Nullable
    val volume: Long,

    @CreationTimestamp
    @Column(updatable = false)
    var createdAt: LocalDateTime =  LocalDateTime.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticker", insertable = false, updatable = false)
    val stock: Stock? = null
)