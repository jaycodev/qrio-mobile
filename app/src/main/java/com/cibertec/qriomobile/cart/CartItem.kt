package com.cibertec.qriomobile.cart

data class CartItem(
    val productId: Long,
    val name: String,
    val unitPrice: Double,
    var quantity: Int,
    val imageRes: Int? = null,
    val imageUrl: String? = null
) {
    fun subtotal(): Double = unitPrice * quantity
}
