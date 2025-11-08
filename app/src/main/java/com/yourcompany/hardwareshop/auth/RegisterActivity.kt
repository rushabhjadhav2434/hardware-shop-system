package com.yourcompany.hardwareshop.auth

import android.content.Intent  // ADD THIS IMPORT
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.yourcompany.hardwareshop.dashboard.DashboardActivity  // ADD THIS IMPORT
import com.yourcompany.hardwareshop.databinding.ActivityRegisterBinding
import com.yourcompany.hardwareshop.viewmodel.AuthViewModel
import com.yourcompany.hardwareshop.viewmodel.SimpleViewModelFactory

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel
        val factory = SimpleViewModelFactory()
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        setupClickListeners()
        observeAuthState()
    }

    private fun setupClickListeners() {
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInputs(name, email, password)) {
                authViewModel.register(email, password, name)
            }
        }
    }

    private fun observeAuthState() {
        authViewModel.registerState.observe(this, Observer { state ->
            when (state) {
                is com.yourcompany.hardwareshop.viewmodel.RegisterState.Loading -> {
                    showLoading(true)
                }
                is com.yourcompany.hardwareshop.viewmodel.RegisterState.Success -> {
                    showLoading(false)
                    showMessage("Registration successful!")
                    navigateToDashboard()
                }
                is com.yourcompany.hardwareshop.viewmodel.RegisterState.Error -> {
                    showLoading(false)
                    showMessage("Registration failed: ${state.message}")
                }
                else -> {
                    showLoading(false)
                }
            }
        })
    }

    private fun validateInputs(name: String, email: String, password: String): Boolean {
        var isValid = true

        if (name.isEmpty()) {
            binding.etName.error = "Name is required"
            isValid = false
        } else {
            binding.etName.error = null
        }

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
        finish() // Close register activity
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) android.view.View.VISIBLE else android.view.View.GONE
        binding.btnRegister.isEnabled = !show
    }
}