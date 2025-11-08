package com.yourcompany.hardwareshop.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.yourcompany.hardwareshop.data.model.Bill
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class BillRepository @Inject constructor() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val billsCollection = db.collection("bills")

    suspend fun getBillsByUser(userId: String): List<Bill> {
        return try {
            val querySnapshot = billsCollection
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            querySnapshot.documents.map { document ->
                document.toObject(Bill::class.java)!!
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getAllBills(): List<Bill> {
        return try {
            val querySnapshot = billsCollection
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            querySnapshot.documents.map { document ->
                document.toObject(Bill::class.java)!!
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun updateBillStatus(billId: String, status: String): Result<Boolean> {
        return try {
            billsCollection.document(billId)
                .update("status", status)
                .await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}