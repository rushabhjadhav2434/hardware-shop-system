package com.yourcompany.hardwareshop.owner

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.yourcompany.hardwareshop.owner.databinding.ActivityOwnerDashboardBinding

class OwnerDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOwnerDashboardBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOwnerDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvWelcome.text = "Welcome, Owner!"

        binding.btnManageProducts.setOnClickListener {
            val intent = Intent(this, OwnerProductsActivity::class.java)
            startActivity(intent)
        }

        binding.btnViewBills.setOnClickListener {
            val intent = Intent(this, OwnerBillsActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, OwnerLoginActivity::class.java))
            finish()
        }
    }
}