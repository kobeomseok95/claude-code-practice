package com.example.product.service

import com.example.product.dto.ProductResponse
import com.example.product.repository.ProductRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val productRepository: ProductRepository
) {
    fun findAll(): List<ProductResponse> =
        productRepository.findAll().map { ProductResponse.from(it) }

    fun findById(id: Long): ProductResponse =
        productRepository.findByIdOrNull(id)
            ?.let { ProductResponse.from(it) }
            ?: throw NoSuchElementException("Product not found: $id")
}
