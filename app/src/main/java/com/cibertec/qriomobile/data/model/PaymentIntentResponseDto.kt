package com.cibertec.qriomobile.data.model

data class PaymentIntentResponseDto(
    val intentId: String,
    val clientSecret: String,
    val status: String
)
