package com.yourcompany.hardwareshop.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourcompany.hardwareshop.data.model.Product
import com.yourcompany.hardwareshop.data.repository.ProductRepository
import kotlinx.coroutines.launch

class ProductViewModel(private val productRepository: ProductRepository) : ViewModel() {

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun loadProducts() {
        _loading.value = true
        viewModelScope.launch {
            try {
                val productList = productRepository.getAllProducts()
                _products.value = productList
                _error.value = ""
            } catch (e: Exception) {
                _error.value = "Failed to load products: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadProductsByCategory(category: String) {
        _loading.value = true
        viewModelScope.launch {
            try {
                val productList = productRepository.getProductsByCategory(category)
                _products.value = productList
                _error.value = ""
            } catch (e: Exception) {
                _error.value = "Failed to load products: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }
}