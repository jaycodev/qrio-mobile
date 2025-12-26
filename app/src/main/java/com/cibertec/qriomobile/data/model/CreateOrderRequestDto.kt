package com.cibertec.qriomobile.data.model

import java.math.BigDecimal

data class CreateOrderRequestDto(
    val tableId: Long,
    val customerId: Long,
    val status: String = "PENDIENTE",
    val total: BigDecimal,
    val people: Int,
    val items: List<CreateOrderItemDto>
)
