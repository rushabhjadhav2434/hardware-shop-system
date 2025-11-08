package com.yourcompany.hardwareshop.auth

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.yourcompany.hardwareshop.data.model.Product
import com.yourcompany.hardwareshop.databinding.ItemProductBinding

class ProductAdapter(
    private var products: List<Product> = emptyList(),
    private val onAddToCart: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    companion object {
        private const val TAG = "ProductAdapter"
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            try {
                binding.tvProductName.text = product.name
                binding.tvProductDescription.text = product.description ?: "No description available"
                binding.tvPrice.text = "₹${"%.2f".format(product.price)}"

                // ✅ Enhanced stock display with clear indicators
                if (product.stock > 0) {
                    binding.tvStock.text = "In Stock: ${product.stock}"
                    binding.tvStock.setTextColor(ContextCompat.getColor(binding.root.context, com.yourcompany.hardwareshop.R.color.green))
                    binding.tvStock.setBackgroundResource(com.yourcompany.hardwareshop.R.drawable.in_stock_background)
                } else {
                    binding.tvStock.text = "OUT OF STOCK"
                    binding.tvStock.setTextColor(ContextCompat.getColor(binding.root.context, com.yourcompany.hardwareshop.R.color.white))
                    binding.tvStock.setBackgroundResource(com.yourcompany.hardwareshop.R.drawable.out_of_stock_background)
                }

                // ✅ Enhanced button states
                val hasStock = product.stock > 0
                binding.btnAddToCart.isEnabled = hasStock
                binding.btnAddToCart.text = if (hasStock) "Add to Cart" else "Out of Stock"
                binding.btnAddToCart.alpha = if (hasStock) 1.0f else 0.6f

                // Change button color for out-of-stock
                if (hasStock) {
                    binding.btnAddToCart.setBackgroundColor(ContextCompat.getColor(binding.root.context, com.yourcompany.hardwareshop.R.color.green))
                } else {
                    binding.btnAddToCart.setBackgroundColor(ContextCompat.getColor(binding.root.context, com.yourcompany.hardwareshop.R.color.red))
                }

                binding.btnAddToCart.setOnClickListener {
                    try {
                        if (hasStock) {
                            Log.d(TAG, "Add to cart button clicked for: ${product.name}")
                            onAddToCart(product)
                        } else {
                            // Show message if user tries to click out-of-stock item
                            android.widget.Toast.makeText(
                                binding.root.context,
                                "❌ ${product.name} is currently out of stock!",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error in add to cart click: ${e.message}", e)
                    }
                }

                Log.d(TAG, "Product bound: ${product.name} - Stock: ${product.stock}, HasStock: $hasStock")
            } catch (e: Exception) {
                Log.e(TAG, "Error binding product ${product.name}: ${e.message}", e)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return try {
            val binding = ItemProductBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            ProductViewHolder(binding)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating view holder: ${e.message}", e)
            throw e
        }
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        try {
            val product = products[position]
            holder.bind(product)
        } catch (e: Exception) {
            Log.e(TAG, "Error in onBindViewHolder: ${e.message}", e)
        }
    }

    override fun getItemCount(): Int = products.size

    fun updateProducts(newProducts: List<Product>) {
        Log.d(TAG, "Updating products: ${newProducts.size} items")
        products = newProducts
        notifyDataSetChanged()
    }
}