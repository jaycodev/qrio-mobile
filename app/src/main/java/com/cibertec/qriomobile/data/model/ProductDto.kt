package com.cibertec.qriomobile.data.model

data class ProductDto(
    val id: Long,
    val imageUrl: String?,
    val name: String,
    val description: String? = null,
    val price: Double,
    val category: Category?,
    val available: Boolean? = null,
    // Recurso local opcional (para datos mock o placeholders)
    val imageRes: Int? = null
) {
    data class Category(
        val id: Long?,
        val name: String?
    )
}
