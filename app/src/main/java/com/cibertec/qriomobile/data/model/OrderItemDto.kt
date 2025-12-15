package com.cibertec.qriomobile.data.model

data class OrderItemDto(
    val id: Long? = null,

    val product_id: Long,
    val quantity: Int,
    val unit_price: Double,
    val subtotal: Double
)
