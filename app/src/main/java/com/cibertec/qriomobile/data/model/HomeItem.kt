package com.cibertec.qriomobile.data.model

sealed class HomeItem {
    data class ProductoItem(val product: ProductDto) : HomeItem()
    data class PromoItem(val promo: PromoUi) : HomeItem()
}
