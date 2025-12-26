package com.cibertec.qriomobile.data.model

data class OfferItemDto(
    val id: Long,
    val restaurantId: Long?,
    val title: String,
    val description: String?,
    val offerDiscountPercentage: Double?,
    val active: Boolean?
)
