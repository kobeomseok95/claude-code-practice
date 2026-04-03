package com.example.order.dto

import java.time.LocalDateTime

data class OrderResponse(
    val id: Long,
    val productId: Long,
    val paymentId: Long,
    val status: String,
    val createdAt: LocalDateTime
)
