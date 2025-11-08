package com.yourcompany.hardwareshop.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.yourcompany.hardwareshop.HardwareShopApp
import com.yourcompany.hardwareshop.databinding.ActivityProductsBinding
import com.yourcompany.hardwareshop.data.model.Product
import com.yourcompany.hardwareshop.viewmodel.CartViewModel
import com.yourcompany.hardwareshop.viewmodel.ProductViewModel
import com.yourcompany.hardwareshop.viewmodel.SimpleViewModelFactory

class ProductsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductsBinding
    private lateinit var productViewModel: ProductViewModel
    private lateinit var cartViewModel: CartViewModel
    private lateinit var productAdapter: ProductAdapter

    companion object {
        private const val TAG = "ProductsActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "ProductsActivity created")

        try {
            setupViewModels()
            setupRecyclerView()
            setupClickListeners()
            observeViewModels()

            // Load products
            productViewModel.loadProducts()
            Log.d(TAG, "Products loading started")
        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate: ${e.message}", e)
            showMessage("Error initializing products screen")
        }
    }

    private fun setupViewModels() {
        try {
            val factory = SimpleViewModelFactory()
            productViewModel = ViewModelProvider(this, factory).get(ProductViewModel::class.java)

            // Use shared CartViewModel from Application
            cartViewModel = (application as HardwareShopApp).cartViewModel
            Log.d(TAG, "ViewModels setup completed - Cart items: ${cartViewModel.getCartItemCount()}")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up ViewModels: ${e.message}", e)
            showMessage("Error setting up ViewModels")
        }
    }

    private fun setupRecyclerView() {
        try {
            productAdapter = ProductAdapter(emptyList()) { product ->
                Log.d(TAG, "Add to cart clicked for product: ${product.name}")
                // Show quantity dialog before adding to cart
                showQuantityDialog(product)
            }

            binding.rvProducts.apply {
                layoutManager = LinearLayoutManager(this@ProductsActivity)
                adapter = productAdapter
                Log.d(TAG, "RecyclerView adapter set")
            }
            Log.d(TAG, "RecyclerView setup completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up RecyclerView: ${e.message}", e)
            showMessage("Error setting up product list")
        }
    }

    private fun setupClickListeners() {
        binding.btnCart.setOnClickListener {
            try {
                val count = cartViewModel.getCartItemCount()
                showMessage("Cart Items: $count")
                Log.d(TAG, "Cart button clicked - count: $count")

                // Navigate to Cart
                val intent = Intent(this, CartActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Log.e(TAG, "Error in cart button: ${e.message}", e)
            }
        }
    }

    private fun observeViewModels() {
        // Observe products
        productViewModel.products.observe(this) { products ->
            Log.d(TAG, "Products loaded: ${products.size} items")
            try {
                productAdapter.updateProducts(products)
                Log.d(TAG, "Products updated in adapter")
            } catch (e: Exception) {
                Log.e(TAG, "Error updating products in adapter: ${e.message}", e)
            }
        }

        productViewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
            Log.d(TAG, "Loading state: $isLoading")
        }

        productViewModel.error.observe(this) { error ->
            if (error.isNotEmpty()) {
                Log.e(TAG, "Error loading products: $error")
                binding.tvError.text = error
                binding.tvError.visibility = android.view.View.VISIBLE
                showMessage(error)
            } else {
                binding.tvError.visibility = android.view.View.GONE
            }
        }

        // Observe cart item count
        cartViewModel.itemCount.observe(this) { count ->
            Log.d(TAG, "Cart item count updated: $count")
            try {
                binding.btnCart.text = "Cart ($count)"
            } catch (e: Exception) {
                Log.e(TAG, "Error updating cart button: ${e.message}", e)
            }
        }
    }

    private fun showQuantityDialog(product: Product) {
        try {
            Log.d(TAG, "Showing quantity dialog for: ${product.name}")

            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Select Quantity")
            builder.setMessage("How many '${product.name}' do you want to add?")

            // Create input field for quantity
            val input = android.widget.EditText(this)
            input.inputType = android.text.InputType.TYPE_CLASS_NUMBER
            input.setText("1")
            input.setSelection(input.text.length)

            builder.setView(input)

            builder.setPositiveButton("Add to Cart") { dialog, which ->
                try {
                    val quantityText = input.text.toString()
                    Log.d(TAG, "Quantity entered: $quantityText")

                    if (quantityText.isNotEmpty()) {
                        val quantity = quantityText.toIntOrNull() ?: 1
                        if (quantity > 0) {
                            if (quantity <= product.stock) {
                                cartViewModel.addToCart(
                                    productId = product.productId,
                                    name = product.name,
                                    price = product.price,
                                    unit = product.unit,
                                    quantity = quantity
                                )
                                showMessage("$quantity ${product.name} added to cart!")
                                Log.d(TAG, "Product added to cart: ${product.name}, quantity: $quantity")
                            } else {
                                showMessage("Not enough stock! Available: ${product.stock}")
                            }
                        } else {
                            showMessage("Please enter a valid quantity")
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error in add to cart dialog: ${e.message}", e)
                    showMessage("Error adding to cart")
                }
            }

            builder.setNegativeButton("Cancel") { dialog, which ->
                dialog.cancel()
                Log.d(TAG, "Quantity dialog cancelled")
            }

            val dialog = builder.create()
            dialog.show()
            Log.d(TAG, "Quantity dialog shown successfully")

        } catch (e: Exception) {
            Log.e(TAG, "Error showing quantity dialog: ${e.message}", e)
            showMessage("Error showing quantity selection")
        }
    }

    private fun showMessage(message: String) {
        try {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Toast message: $message")
        } catch (e: Exception) {
            Log.e(TAG, "Error showing toast: ${e.message}", e)
        }
    }
}