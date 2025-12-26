package com.cibertec.qriomobile.data.model

import java.math.BigDecimal

// Representa OrderDetailResponse (API)
data class OrderDetailDto(
    val id: Long,
    val tableId: Long,
    val customerId: Long,
    val status: String,
    val total: BigDecimal,
    val people: Int,
    val items: List<OrderItem>
) {
    data class OrderItem(
        val productId: Long,
        val productName: String,
        val productImageUrl: String?,
        val quantity: Int,
        val unitPrice: BigDecimal,
        val subtotal: BigDecimal
    )
}
