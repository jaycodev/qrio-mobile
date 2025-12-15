package com.cibertec.qriomobile.data.model

data class PaymentMethodDto(
    val id: Long? = null,
    val customer_id: Long,
    val type: String,
    val payment_token: String,
    val last4: String? = null,
    val brand: String? = null,
    val created_at: String? = null
)
