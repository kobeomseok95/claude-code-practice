package com.example.product.dto

import com.example.product.entity.Product
import java.time.LocalDateTime

data class ProductResponse(
    val id: Long,
    val name: String,
    val price: Long,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(product: Product) = ProductResponse(
            id = product.id,
            name = product.name,
            price = product.price,
            createdAt = product.createdAt
        )
    }
}
