package com.cibertec.qriomobile.data.model

data class ProductDto(
    val id: Long,
    val category_id: Long,
    val name: String,
    val description: String? = null,
    val price: Double,
    // URL remoto (backend)
    val image_url: String?,
    // Recurso local opcional (para datos mock o placeholders)
    val image_res: Int? = null
)
