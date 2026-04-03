package com.example.order.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val productId: Long,

    val paymentId: Long,

    val status: String = "CREATED",

    val createdAt: LocalDateTime = LocalDateTime.now()
)
