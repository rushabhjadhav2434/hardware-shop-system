package com.yourcompany.hardwareshop.data.model

data class User(
    val userId: String = "",
    val email: String = "",
    val name: String = "",
    val userType: String = "customer" // "customer" or "owner"
)