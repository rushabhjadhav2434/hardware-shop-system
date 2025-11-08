package com.yourcompany.hardwareshop

import android.app.Application
import com.yourcompany.hardwareshop.data.repository.CartRepository
import com.yourcompany.hardwareshop.viewmodel.CartViewModel

class HardwareShopApp : Application() {

    // Shared CartRepository and CartViewModel
    private val cartRepository: CartRepository by lazy { CartRepository() }
    val cartViewModel: CartViewModel by lazy { CartViewModel(cartRepository) }
}