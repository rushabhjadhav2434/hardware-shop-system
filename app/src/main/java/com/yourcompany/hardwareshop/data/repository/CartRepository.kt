package com.yourcompany.hardwareshop.data.repository

import com.yourcompany.hardwareshop.data.model.CartItem
import javax.inject.Inject

class CartRepository @Inject constructor() {

    private val cartItems = mutableListOf<CartItem>()

    fun addToCart(productId: String, name: String, price: Double, unit: String, quantity: Int = 1) {
        val existingItem = cartItems.find { it.productId == productId }

        if (existingItem != null) {
            // Update quantity if item already exists
            existingItem.quantity += quantity
        } else {
            // Add new item to cart
            val newItem = CartItem(
                productId = productId,
                name = name,
                price = price,
                unit = unit,
                quantity = quantity
            )
            cartItems.add(newItem)
        }
    }

    fun updateQuantity(productId: String, newQuantity: Int) {
        val item = cartItems.find { it.productId == productId }
        item?.quantity = newQuantity

        // Remove item if quantity is 0 or less
        if (newQuantity <= 0) {
            removeFromCart(productId)
        }
    }

    fun removeFromCart(productId: String) {
        cartItems.removeAll { it.productId == productId }
    }

    fun getCartItems(): List<CartItem> {
        return cartItems.map { it.copy(total = it.calculateTotal()) }
    }

    fun getCartTotal(): Double {
        return cartItems.sumOf { it.calculateTotal() }
    }

    fun clearCart() {
        cartItems.clear()
    }

    fun getCartItemCount(): Int {
        return cartItems.size
    }
}