package com.dyk.test.dto

import java.time.LocalDateTime

data class PriceResponse(
    val time: LocalDateTime,
    val price: Float
)