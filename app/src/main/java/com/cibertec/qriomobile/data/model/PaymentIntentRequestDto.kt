package com.cibertec.qriomobile.data.model

import java.math.BigDecimal

data class PaymentIntentRequestDto(
    val amount: BigDecimal,
    val currency: String = "pen",
    val description: String? = null,
    val orderId: Long? = null,
    val receiptEmail: String? = null
)
