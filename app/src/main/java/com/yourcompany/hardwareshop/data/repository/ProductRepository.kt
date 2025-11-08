package com.yourcompany.hardwareshop.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.yourcompany.hardwareshop.data.model.Product
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProductRepository @Inject constructor() {
    private val db = FirebaseFirestore.getInstance()
    private val productsCollection = db.collection("products")

    suspend fun getAllProducts(): List<Product> {
        return try {
            val querySnapshot = productsCollection
                .orderBy("name", Query.Direction.ASCENDING) // ✅ Show ALL products
                .get()
                .await()

            querySnapshot.documents.map { document ->
                document.toObject(Product::class.java)!!.copy(
                    productId = document.id
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getProductById(productId: String): Product? {
        return try {
            val document = productsCollection.document(productId).get().await()
            if (document.exists()) {
                document.toObject(Product::class.java)!!.copy(productId = document.id)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getProductsByCategory(category: String): List<Product> {
        return try {
            val querySnapshot = productsCollection
                .whereEqualTo("category", category)
                .orderBy("name", Query.Direction.ASCENDING) // ✅ Show ALL products
                .get()
                .await()

            querySnapshot.documents.map { document ->
                document.toObject(Product::class.java)!!.copy(
                    productId = document.id
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}