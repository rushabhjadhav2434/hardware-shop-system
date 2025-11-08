package com.yourcompany.hardwareshop.data.model

data class Product(
    val productId: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val unit: String = "",
    val stock: Int = 0,
    val description: String = "",
    val category: String = ""
) {
    // Empty constructor for Firestore
    constructor() : this("", "", 0.0, "", 0, "", "")
}