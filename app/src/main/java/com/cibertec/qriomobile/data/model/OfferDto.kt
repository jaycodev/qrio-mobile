package com.cibertec.qriomobile.data.model

data class OfferDto(
    val id: Long? = null,
    val title: String,
    val description: String? = null,
    val offer_discount_percentage: Double? = null,
    val active: Boolean? = true
)
