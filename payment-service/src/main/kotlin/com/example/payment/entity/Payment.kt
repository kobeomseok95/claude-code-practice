package com.example.payment.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "payments")
class Payment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val productId: Long,

    @Column(nullable = false)
    val amount: Long,

    @Column(nullable = false)
    val status: String = "COMPLETED",

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
