package com.cibertec.qriomobile.data.model

import java.math.BigDecimal

data class CreateOrderRequestDto(
    val branchId: Long,
    val tableId: Long,
    val customerId: Long,
    val status: String = "PENDIENTE",
    val total: BigDecimal,
    val people: Int,
    val orderDate: String? = null, // Agregado por si el server lo requiere
    val items: List<CreateOrderItemDto>,
    val paymentIntentId: String? = null
)
