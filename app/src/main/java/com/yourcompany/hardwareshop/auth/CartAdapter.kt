package com.yourcompany.hardwareshop.auth

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yourcompany.hardwareshop.data.model.CartItem
import com.yourcompany.hardwareshop.databinding.ItemCartBinding

class CartAdapter(
    private var cartItems: List<CartItem> = emptyList(),
    private val onQuantityChanged: (String, Int) -> Unit,
    private val onItemRemoved: (String) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    companion object {
        private const val TAG = "CartAdapter"
    }

    inner class CartViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem) {
            try {
                binding.tvProductName.text = cartItem.name
                binding.tvPrice.text = "₹${"%.2f".format(cartItem.price)} each"
                binding.tvQuantity.text = cartItem.quantity.toString()
                binding.tvTotal.text = "Total: ₹${"%.2f".format(cartItem.calculateTotal())}"

                // Quantity buttons
                binding.btnDecrease.setOnClickListener {
                    val newQuantity = cartItem.quantity - 1
                    if (newQuantity >= 0) {
                        onQuantityChanged(cartItem.productId, newQuantity)
                    }
                }

                binding.btnIncrease.setOnClickListener {
                    val newQuantity = cartItem.quantity + 1
                    onQuantityChanged(cartItem.productId, newQuantity)
                }

                // Remove button
                binding.btnRemove.setOnClickListener {
                    onItemRemoved(cartItem.productId)
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error binding cart item: ${e.message}", e)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]
        holder.bind(cartItem)
    }

    override fun getItemCount(): Int = cartItems.size

    fun updateCartItems(newCartItems: List<CartItem>) {
        cartItems = newCartItems
        notifyDataSetChanged()
    }
}