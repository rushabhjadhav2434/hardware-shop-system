package com.yourcompany.hardwareshop.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.yourcompany.hardwareshop.HardwareShopApp
import com.yourcompany.hardwareshop.databinding.ActivityCartBinding
import com.yourcompany.hardwareshop.viewmodel.BillViewModel
import com.yourcompany.hardwareshop.viewmodel.CartViewModel
import com.yourcompany.hardwareshop.viewmodel.SimpleViewModelFactory

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var cartViewModel: CartViewModel
    private lateinit var billViewModel: BillViewModel
    private lateinit var cartAdapter: CartAdapter

    companion object {
        private const val TAG = "CartActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "CartActivity created")

        try {
            setupViewModels()
            setupRecyclerView()
            setupClickListeners()
            observeViewModels()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate: ${e.message}", e)
            showMessage("Error initializing cart screen")
        }
    }

    private fun setupViewModels() {
        try {
            // Use shared CartViewModel from Application
            cartViewModel = (application as HardwareShopApp).cartViewModel

            // Create BillViewModel
            val factory = SimpleViewModelFactory()
            billViewModel = ViewModelProvider(this, factory).get(BillViewModel::class.java)

            Log.d(TAG, "ViewModels setup completed - Cart items: ${cartViewModel.getCartItemCount()}")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up ViewModels: ${e.message}", e)
            showMessage("Error setting up cart")
        }
    }

    private fun setupRecyclerView() {
        try {
            cartAdapter = CartAdapter(
                emptyList(),
                onQuantityChanged = { productId, newQuantity ->
                    cartViewModel.updateQuantity(productId, newQuantity)
                },
                onItemRemoved = { productId ->
                    cartViewModel.removeFromCart(productId)
                }
            )

            binding.rvCartItems.apply {
                layoutManager = LinearLayoutManager(this@CartActivity)
                adapter = cartAdapter
            }
            Log.d(TAG, "RecyclerView setup completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up RecyclerView: ${e.message}", e)
            showMessage("Error setting up cart items")
        }
    }

    private fun setupClickListeners() {
        binding.btnClearCart.setOnClickListener {
            try {
                cartViewModel.clearCart()
                showMessage("Cart cleared")
            } catch (e: Exception) {
                Log.e(TAG, "Error clearing cart: ${e.message}", e)
            }
        }

        binding.btnGenerateBill.setOnClickListener {
            try {
                generateBill()
            } catch (e: Exception) {
                Log.e(TAG, "Error generating bill: ${e.message}", e)
                showMessage("Error generating bill")
            }
        }
    }

    private fun observeViewModels() {
        // Observe cart items
        cartViewModel.cartItems.observe(this) { cartItems ->
            Log.d(TAG, "Cart items updated: ${cartItems.size} items")
            cartAdapter.updateCartItems(cartItems)
            updateUI(cartItems.isNotEmpty())
        }

        cartViewModel.cartTotal.observe(this) { total ->
            Log.d(TAG, "Cart total updated: $total")
            binding.tvTotalAmount.text = "₹${"%.2f".format(total)}"
            binding.btnGenerateBill.isEnabled = total > 0
        }

        // Observe bill generation
        billViewModel.billGenerated.observe(this) { generated ->
            if (generated) {
                showMessage("Bill generated successfully!")
                cartViewModel.clearCart()
                // Navigate back to products or dashboard
                finish()
            }
        }

        billViewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
        }

        billViewModel.error.observe(this) { error ->
            if (error.isNotEmpty()) {
                Log.e(TAG, "Bill error: $error")
                showMessage(error)
            }
        }
    }

    private fun updateUI(hasItems: Boolean) {
        if (hasItems) {
            binding.tvEmptyCart.visibility = android.view.View.GONE
            binding.rvCartItems.visibility = android.view.View.VISIBLE
        } else {
            binding.tvEmptyCart.visibility = android.view.View.VISIBLE
            binding.rvCartItems.visibility = android.view.View.GONE
            binding.tvTotalAmount.text = "₹0.00"
            binding.btnGenerateBill.isEnabled = false
        }
    }

    private fun generateBill() {
        val cartItems = cartViewModel.cartItems.value ?: emptyList()
        val total = cartViewModel.cartTotal.value ?: 0.0

        if (cartItems.isNotEmpty() && total > 0) {
            Log.d(TAG, "Generating bill for ${cartItems.size} items, total: $total")
            billViewModel.generateBill(cartItems, total)
        } else {
            showMessage("Cannot generate bill - cart is empty")
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        Log.d(TAG, "Toast message: $message")
    }
}