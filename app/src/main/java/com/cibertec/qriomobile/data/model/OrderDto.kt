package com.cibertec.qriomobile.data.model

data class OrderDto(
    val id: Long? = null,
    val code: String? = null,

    val status: String? = "PENDIENTE",
    val total: Double? = null,
    val people: Int? = null,
    val created_at: String? = null
)
