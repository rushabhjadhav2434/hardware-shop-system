package com.yourcompany.hardwareshop.owner

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.yourcompany.hardwareshop.owner.databinding.ActivityOwnerProductsBinding

class OwnerProductsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOwnerProductsBinding
    private val db = FirebaseFirestore.getInstance()
    private val productsCollection = db.collection("products")
    private val productList = mutableListOf<Product>()
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOwnerProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadProducts()

        binding.btnAddProduct.setOnClickListener {
            showAddProductDialog()
        }
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(productList) { product ->
            showEditProductDialog(product)
        }
        binding.rvProducts.layoutManager = LinearLayoutManager(this)
        binding.rvProducts.adapter = adapter
    }

    private fun loadProducts() {
        productsCollection.get()
            .addOnSuccessListener { querySnapshot ->
                productList.clear()
                for (document in querySnapshot) {
                    val product = Product(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        price = document.getDouble("price") ?: 0.0,
                        unit = document.getString("unit") ?: "",
                        stock = document.getLong("stock")?.toInt() ?: 0,
                        description = document.getString("description") ?: "",
                        category = document.getString("category") ?: ""
                    )
                    productList.add(product)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading products: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showAddProductDialog() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Add New Product")

        val view = layoutInflater.inflate(R.layout.dialog_add_product, null)
        val etName = view.findViewById<android.widget.EditText>(R.id.etName)
        val etPrice = view.findViewById<android.widget.EditText>(R.id.etPrice)
        val etUnit = view.findViewById<android.widget.EditText>(R.id.etUnit)
        val etStock = view.findViewById<android.widget.EditText>(R.id.etStock)
        val etDescription = view.findViewById<android.widget.EditText>(R.id.etDescription)
        val etCategory = view.findViewById<android.widget.EditText>(R.id.etCategory)

        builder.setView(view)

        builder.setPositiveButton("Add") { dialog, which ->
            val name = etName.text.toString()
            val price = etPrice.text.toString().toDoubleOrNull() ?: 0.0
            val unit = etUnit.text.toString()
            val stock = etStock.text.toString().toIntOrNull() ?: 0
            val description = etDescription.text.toString()
            val category = etCategory.text.toString()

            if (name.isNotEmpty() && price > 0) {
                addProduct(name, price, unit, stock, description, category)
            } else {
                Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun addProduct(name: String, price: Double, unit: String, stock: Int, description: String, category: String) {
        val productData = hashMapOf(
            "name" to name,
            "price" to price,
            "unit" to unit,
            "stock" to stock,
            "description" to description,
            "category" to category
        )

        productsCollection.add(productData)
            .addOnSuccessListener {
                Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show()
                loadProducts()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error adding product: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showEditProductDialog(product: Product) {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Edit Product")

        val view = layoutInflater.inflate(R.layout.dialog_add_product, null)
        val etName = view.findViewById<android.widget.EditText>(R.id.etName)
        val etPrice = view.findViewById<android.widget.EditText>(R.id.etPrice)
        val etUnit = view.findViewById<android.widget.EditText>(R.id.etUnit)
        val etStock = view.findViewById<android.widget.EditText>(R.id.etStock)
        val etDescription = view.findViewById<android.widget.EditText>(R.id.etDescription)
        val etCategory = view.findViewById<android.widget.EditText>(R.id.etCategory)

        // Fill with existing data
        etName.setText(product.name)
        etPrice.setText(product.price.toString())
        etUnit.setText(product.unit)
        etStock.setText(product.stock.toString())
        etDescription.setText(product.description)
        etCategory.setText(product.category)

        builder.setView(view)

        builder.setPositiveButton("Update") { dialog, which ->
            val name = etName.text.toString()
            val price = etPrice.text.toString().toDoubleOrNull() ?: 0.0
            val unit = etUnit.text.toString()
            val stock = etStock.text.toString().toIntOrNull() ?: 0
            val description = etDescription.text.toString()
            val category = etCategory.text.toString()

            if (name.isNotEmpty() && price > 0) {
                updateProduct(product.id, name, price, unit, stock, description, category)
            }
        }

        builder.setNeutralButton("Delete") { dialog, which ->
            deleteProduct(product.id)
        }

        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun updateProduct(productId: String, name: String, price: Double, unit: String, stock: Int, description: String, category: String) {
        val productData = hashMapOf(
            "name" to name,
            "price" to price,
            "unit" to unit,
            "stock" to stock,
            "description" to description,
            "category" to category
        )

        productsCollection.document(productId).update(productData as Map<String, Any>)
            .addOnSuccessListener {
                Toast.makeText(this, "Product updated successfully", Toast.LENGTH_SHORT).show()
                loadProducts()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error updating product: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteProduct(productId: String) {
        productsCollection.document(productId).delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Product deleted successfully", Toast.LENGTH_SHORT).show()
                loadProducts()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error deleting product: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

data class Product(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val unit: String = "",
    val stock: Int = 0,
    val description: String = "",
    val category: String = ""
)

class ProductAdapter(
    private val products: List<Product>,
    private val onItemClick: (Product) -> Unit
) : androidx.recyclerview.widget.RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    class ViewHolder(val view: android.view.View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val textView: android.widget.TextView = view.findViewById(android.R.id.text1)
        val textView2: android.widget.TextView = view.findViewById(android.R.id.text2)
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]

        // ✅ Enhanced display with stock status
        holder.textView.text = "${product.name} - ₹${product.price}"

        if (product.stock > 0) {
            holder.textView2.text = "Stock: ${product.stock} | ${product.category}"
            holder.textView2.setTextColor(0xFF4CAF50.toInt()) // Green for in stock
        } else {
            holder.textView2.text = "OUT OF STOCK | ${product.category}"
            holder.textView2.setTextColor(0xFFF44336.toInt()) // Red for out of stock
        }

        holder.view.setOnClickListener {
            onItemClick(product)
        }
    }

    override fun getItemCount() = products.size
}