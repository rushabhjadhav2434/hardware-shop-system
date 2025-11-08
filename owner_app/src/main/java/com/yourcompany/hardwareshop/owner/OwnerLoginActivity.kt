package com.yourcompany.hardwareshop.owner

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.yourcompany.hardwareshop.owner.databinding.ActivityOwnerLoginBinding

class OwnerLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOwnerLoginBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOwnerLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if already logged in
        if (auth.currentUser != null) {
            startActivity(Intent(this, OwnerDashboardActivity::class.java))
            finish()
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.progressBar.visibility = View.VISIBLE
            binding.btnLogin.isEnabled = false

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true

                    if (task.isSuccessful) {
                        Toast.makeText(this, "Owner login successful!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, OwnerDashboardActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}