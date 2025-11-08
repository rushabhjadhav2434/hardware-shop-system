package com.yourcompany.hardwareshop.data.model

data class CartItem(
    val productId: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val unit: String = "",
    var quantity: Int = 0,
    val total: Double = 0.0
) {
    fun calculateTotal(): Double {
        return price * quantity
    }
}