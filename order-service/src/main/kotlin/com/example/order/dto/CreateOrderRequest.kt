package com.example.order.dto

data class CreateOrderRequest(
    val productId: Long,
    val paymentId: Long
)
