package com.cibertec.qriomobile.data.model

import java.math.BigDecimal

// Representa OrderListResponse (API)
data class OrderDto(
    val id: Long,
    val code: String,
    val table: Table,
    val customer: Customer,
    val status: String,
    val total: BigDecimal,
    val people: Int,
    val itemCount: Long,
    val stripeClientSecret: String? = null // Agregado para Stripe
) {
    data class Table(
        val id: Long,
        val number: Int
    )
    data class Customer(
        val id: Long,
        val code: String,
        val name: String
    )
}
