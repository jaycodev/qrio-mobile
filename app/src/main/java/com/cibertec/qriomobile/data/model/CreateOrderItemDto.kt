package com.cibertec.qriomobile.data.model

import java.math.BigDecimal

data class CreateOrderItemDto(
    val orderId: Long = 1L, // backend valida >=1 pero no lo usa en create
    val productId: Long,
    val quantity: Int,
    val unitPrice: BigDecimal
)
