    package com.cibertec.qriomobile.data.model

    data class ProductDto(
        val id: Long,
        val category_id: Long,
        val name: String,
        val description: String? = null,
        val price: Double,
        val image_url: Int
    )
