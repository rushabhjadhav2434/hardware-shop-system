package com.yourcompany.hardwareshop.dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.yourcompany.hardwareshop.auth.LoginActivity
import com.yourcompany.hardwareshop.auth.ProductsActivity
import com.yourcompany.hardwareshop.auth.CartActivity
import com.yourcompany.hardwareshop.auth.BillHistoryActivity  // ADD THIS IMPORT
import com.yourcompany.hardwareshop.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupClickListeners()
    }

    private fun setupUI() {
        val currentUser = auth.currentUser
        val welcomeText = "Welcome, ${currentUser?.email ?: "User"}!"
        binding.tvWelcome.text = welcomeText
    }

    private fun setupClickListeners() {
        binding.btnProducts.setOnClickListener {
            val intent = Intent(this, ProductsActivity::class.java)
            startActivity(intent)
        }

        binding.btnCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }

        binding.btnBills.setOnClickListener {
            val intent = Intent(this, BillHistoryActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            showMessage("Logged out successfully")
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}