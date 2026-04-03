package com.example.payment.dto

import java.time.LocalDateTime

data class PaymentResponse(
    val id: Long,
    val productId: Long,
    val amount: Long,
    val status: String,
    val createdAt: LocalDateTime
)
