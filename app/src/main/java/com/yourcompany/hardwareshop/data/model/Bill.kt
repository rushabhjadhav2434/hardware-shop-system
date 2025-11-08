package com.yourcompany.hardwareshop.data.model

import java.util.Date

data class Bill(
    val billId: String = "",
    val userId: String = "",
    val userName: String = "",
    val items: List<CartItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val timestamp: Date = Date(),
    val status: String = "Pending" // "Pending" or "Completed"
) {
    // Empty constructor for Firestore
    constructor() : this("", "", "", emptyList(), 0.0, Date(), "Pending")
}