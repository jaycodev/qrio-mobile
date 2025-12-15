package com.cibertec.qriomobile.data.model

data class PromoUi(
    val id: Long,
    val name: String,
    val imageUrl: Int,
    val priceOriginal: Double,
    val discountPercent: Double?,
    val priceFinal: Double
)
