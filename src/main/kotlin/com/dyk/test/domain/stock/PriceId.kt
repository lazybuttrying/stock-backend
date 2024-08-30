package com.dyk.test.domain.stock
import java.io.Serializable
import java.time.LocalDateTime
import jakarta.persistence.Embeddable

@Embeddable
data class PriceId(
    val ticker: String = "",          // 종목 코드
    val time: LocalDateTime = LocalDateTime.now() // 타임스탬프
) : Serializable

