package com.example.payment.service

import com.example.payment.dto.CreatePaymentRequest
import com.example.payment.dto.PaymentResponse
import com.example.payment.entity.Payment
import com.example.payment.repository.PaymentRepository
import org.springframework.stereotype.Service

@Service
class PaymentService(
    private val paymentRepository: PaymentRepository
) {

    fun getAllPayments(): List<PaymentResponse> =
        paymentRepository.findAll().map { it.toResponse() }

    fun getPaymentById(id: Long): PaymentResponse =
        paymentRepository.findById(id)
            .orElseThrow { NoSuchElementException("Payment not found with id: $id") }
            .toResponse()

    fun createPayment(request: CreatePaymentRequest): PaymentResponse {
        val payment = Payment(
            productId = request.productId,
            amount = request.amount
        )
        return paymentRepository.save(payment).toResponse()
    }

    private fun Payment.toResponse() = PaymentResponse(
        id = id,
        productId = productId,
        amount = amount,
        status = status,
        createdAt = createdAt
    )
}
