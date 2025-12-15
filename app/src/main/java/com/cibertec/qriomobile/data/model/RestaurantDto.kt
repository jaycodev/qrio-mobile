package com.cibertec.qriomobile.data.model

data class RestaurantDto(
    val id: Long? = null,

    val name: String,
    val description: String? = null,
    val logo_url: Int,
    val is_active: Boolean? = false,

)
