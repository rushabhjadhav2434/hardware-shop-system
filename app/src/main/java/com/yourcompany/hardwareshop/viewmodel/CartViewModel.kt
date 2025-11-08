package com.yourcompany.hardwareshop.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yourcompany.hardwareshop.data.model.CartItem
import com.yourcompany.hardwareshop.data.repository.CartRepository

class CartViewModel(private val cartRepository: CartRepository) : ViewModel() {

    private val _cartItems = MutableLiveData<List<CartItem>>()
    val cartItems: LiveData<List<CartItem>> get() = _cartItems

    private val _cartTotal = MutableLiveData<Double>()
    val cartTotal: LiveData<Double> get() = _cartTotal

    private val _itemCount = MutableLiveData<Int>()
    val itemCount: LiveData<Int> get() = _itemCount

    init {
        updateCartData()
    }

    fun addToCart(productId: String, name: String, price: Double, unit: String, quantity: Int = 1) {
        cartRepository.addToCart(productId, name, price, unit, quantity)
        updateCartData()
    }

    fun updateQuantity(productId: String, newQuantity: Int) {
        cartRepository.updateQuantity(productId, newQuantity)
        updateCartData()
    }

    fun removeFromCart(productId: String) {
        cartRepository.removeFromCart(productId)
        updateCartData()
    }

    fun clearCart() {
        cartRepository.clearCart()
        updateCartData()
    }

    private fun updateCartData() {
        _cartItems.value = cartRepository.getCartItems()
        _cartTotal.value = cartRepository.getCartTotal()
        _itemCount.value = cartRepository.getCartItemCount()
    }

    fun getCartItemCount(): Int {
        return cartRepository.getCartItemCount()
    }
}