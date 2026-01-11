package com.cibertec.qriomobile.cart

object CartManager {
    private val items = mutableListOf<CartItem>()

    @Volatile var tableNumber: Int = 0
    @Volatile var tableId: Long = 0L
    @Volatile var branchId: Long = 0L

    fun add(productId: Long, name: String, price: Double, quantity: Int, imageRes: Int? = null, imageUrl: String? = null) {
        val existing = items.find { it.productId == productId }
        if (existing != null) {
            existing.quantity += quantity
        } else {
            items.add(CartItem(productId, name, price, quantity, imageRes, imageUrl))
        }
    }

    fun remove(productId: Long) {
        items.removeAll { it.productId == productId }
    }

    fun clear() {
        items.clear()
    }

    fun getItems(): List<CartItem> = items.toList()

    fun total(): Double = items.sumOf { it.subtotal() }

    fun count(): Int = items.sumOf { it.quantity }
}
