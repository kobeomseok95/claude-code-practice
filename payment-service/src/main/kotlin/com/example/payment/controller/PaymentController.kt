package com.example.payment.controller

import com.example.payment.dto.CreatePaymentRequest
import com.example.payment.dto.PaymentResponse
import com.example.payment.service.PaymentService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/payments")
class PaymentController(
    private val paymentService: PaymentService
) {

    @GetMapping
    fun getAllPayments(): List<PaymentResponse> =
        paymentService.getAllPayments()

    @GetMapping("/{id}")
    fun getPaymentById(@PathVariable id: Long): PaymentResponse =
        paymentService.getPaymentById(id)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createPayment(@RequestBody request: CreatePaymentRequest): PaymentResponse =
        paymentService.createPayment(request)
}
