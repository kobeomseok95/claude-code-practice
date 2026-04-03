package com.example.order.service

import com.example.order.dto.CreateOrderRequest
import com.example.order.dto.OrderResponse
import com.example.order.entity.Order
import com.example.order.repository.OrderRepository
import org.springframework.stereotype.Service

@Service
class OrderService(private val orderRepository: OrderRepository) {

    fun getAllOrders(): List<OrderResponse> =
        orderRepository.findAll().map { it.toResponse() }

    fun getOrderById(id: Long): OrderResponse =
        orderRepository.findById(id)
            .orElseThrow { NoSuchElementException("Order not found: $id") }
            .toResponse()

    fun createOrder(request: CreateOrderRequest): OrderResponse {
        val order = Order(
            productId = request.productId,
            paymentId = request.paymentId
        )
        return orderRepository.save(order).toResponse()
    }

    private fun Order.toResponse() = OrderResponse(
        id = id,
        productId = productId,
        paymentId = paymentId,
        status = status,
        createdAt = createdAt
    )
}
