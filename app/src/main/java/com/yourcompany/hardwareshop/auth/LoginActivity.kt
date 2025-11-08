package com.yourcompany.hardwareshop.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.yourcompany.hardwareshop.dashboard.DashboardActivity  // ADD THIS IMPORT
import com.yourcompany.hardwareshop.databinding.ActivityLoginBinding
import com.yourcompany.hardwareshop.viewmodel.AuthViewModel
import com.yourcompany.hardwareshop.viewmodel.SimpleViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel
        val factory = SimpleViewModelFactory()
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        setupClickListeners()
        observeAuthState()

        // Check if user is already logged in
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        val currentUser = authViewModel.getCurrentUser()
        if (currentUser != null) {
            navigateToDashboard()
        }
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInputs(email, password)) {
                authViewModel.login(email, password)
            }
        }

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeAuthState() {
        authViewModel.loginState.observe(this, Observer { state ->
            when (state) {
                is com.yourcompany.hardwareshop.viewmodel.LoginState.Loading -> {
                    showLoading(true)
                }
                is com.yourcompany.hardwareshop.viewmodel.LoginState.Success -> {
                    showLoading(false)
                    showMessage("Login successful!")
                    navigateToDashboard()
                }
                is com.yourcompany.hardwareshop.viewmodel.LoginState.Error -> {
                    showLoading(false)
                    showMessage("Login failed: ${state.message}")
                }
                else -> {
                    showLoading(false)
                }
            }
        })
    }

    private fun validateInputs(email: String, password: String): Boolean {
        var isValid = true

        if (email.isEmpty()) {
            binding.etEmail.error = "Email is required"
            isValid = false
        } else {
            binding.etEmail.error = null
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            binding.etPassword.error = "Password must be at least 6 characters"
            isValid = false
        } else {
            binding.etPassword.error = null
        }

        return isValid
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish() // Close login activity so user can't go back
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) android.view.View.VISIBLE else android.view.View.GONE
        binding.btnLogin.isEnabled = !show
    }
}