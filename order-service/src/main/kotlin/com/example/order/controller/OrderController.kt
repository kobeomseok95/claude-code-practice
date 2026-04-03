package com.example.order.controller

import com.example.order.dto.CreateOrderRequest
import com.example.order.dto.OrderResponse
import com.example.order.service.OrderService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/orders")
class OrderController(private val orderService: OrderService) {

    @GetMapping
    fun getAllOrders(): List<OrderResponse> = orderService.getAllOrders()

    @GetMapping("/{id}")
    fun getOrderById(@PathVariable id: Long): OrderResponse = orderService.getOrderById(id)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createOrder(@RequestBody request: CreateOrderRequest): OrderResponse =
        orderService.createOrder(request)
}
