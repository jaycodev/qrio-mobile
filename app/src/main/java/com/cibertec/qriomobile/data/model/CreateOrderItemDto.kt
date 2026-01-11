package com.cibertec.qriomobile.data.model

import java.math.BigDecimal

data class CreateOrderItemDto(
    // API requiere orderId >= 1 en creación (no se usa en backend)
    val orderId: Long = 1L,
    val productId: Long,
    val quantity: Int,
    val unitPrice: BigDecimal
)
