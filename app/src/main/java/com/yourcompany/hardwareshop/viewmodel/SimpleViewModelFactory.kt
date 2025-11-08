package com.yourcompany.hardwareshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yourcompany.hardwareshop.data.repository.AuthRepository
import com.yourcompany.hardwareshop.data.repository.BillRepository
import com.yourcompany.hardwareshop.data.repository.CartRepository
import com.yourcompany.hardwareshop.data.repository.ProductRepository

@Suppress("UNCHECKED_CAST")
class SimpleViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(AuthRepository()) as T
            }
            modelClass.isAssignableFrom(ProductViewModel::class.java) -> {
                ProductViewModel(ProductRepository()) as T
            }
            modelClass.isAssignableFrom(CartViewModel::class.java) -> {
                CartViewModel(CartRepository()) as T
            }
            modelClass.isAssignableFrom(BillViewModel::class.java) -> {
                BillViewModel(BillRepository()) as T
            }
            else -> {
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}