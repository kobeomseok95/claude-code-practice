package com.example.payment.dto

data class CreatePaymentRequest(
    val productId: Long,
    val amount: Long
)
