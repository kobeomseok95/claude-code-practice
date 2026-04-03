package com.example.product.controller

import com.example.product.dto.ProductResponse
import com.example.product.service.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")
class ProductController(
    private val productService: ProductService
) {
    @GetMapping
    fun findAll(): ResponseEntity<List<ProductResponse>> =
        ResponseEntity.ok(productService.findAll())

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<ProductResponse> =
        ResponseEntity.ok(productService.findById(id))
}
