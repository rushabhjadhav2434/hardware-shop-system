package com.yourcompany.hardwareshop.auth

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.yourcompany.hardwareshop.databinding.ActivityBillHistoryBinding
import com.yourcompany.hardwareshop.viewmodel.BillViewModel
import com.yourcompany.hardwareshop.viewmodel.SimpleViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class BillHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBillHistoryBinding
    private lateinit var billViewModel: BillViewModel
    private lateinit var billAdapter: BillAdapter
    private val auth = FirebaseAuth.getInstance()

    companion object {
        private const val TAG = "BillHistoryActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBillHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "BillHistoryActivity created")

        try {
            setupViewModel()
            setupRecyclerView()
            observeViewModel()
            loadBills()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate: ${e.message}", e)
            showMessage("Error initializing bill history")
        }
    }

    private fun setupViewModel() {
        try {
            val factory = SimpleViewModelFactory()
            billViewModel = ViewModelProvider(this, factory).get(BillViewModel::class.java)
            Log.d(TAG, "BillViewModel setup completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up ViewModel: ${e.message}", e)
            showMessage("Error setting up bill history")
        }
    }

    private fun setupRecyclerView() {
        try {
            billAdapter = BillAdapter(emptyList())

            binding.rvBills.apply {
                layoutManager = LinearLayoutManager(this@BillHistoryActivity)
                adapter = billAdapter
            }
            Log.d(TAG, "RecyclerView setup completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up RecyclerView: ${e.message}", e)
            showMessage("Error setting up bill list")
        }
    }

    private fun observeViewModel() {
        billViewModel.bills.observe(this) { bills ->
            Log.d(TAG, "Bills observed: ${bills.size} bills")
            billAdapter.updateBills(bills)
            updateUI(bills.isNotEmpty())
        }

        billViewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
            Log.d(TAG, "Loading state: $isLoading")
        }

        billViewModel.error.observe(this) { error ->
            if (error.isNotEmpty()) {
                Log.e(TAG, "Error loading bills: $error")
                showMessage(error)
                updateUI(false)
            }
        }
    }

    private fun loadBills() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            Log.d(TAG, "Loading bills for user: ${currentUser.uid}, email: ${currentUser.email}")
            billViewModel.loadUserBills(currentUser.uid)
        } else {
            showMessage("User not logged in")
            Log.e(TAG, "User not logged in")
            updateUI(false)
        }
    }

    private fun updateUI(hasBills: Boolean) {
        Log.d(TAG, "Updating UI - hasBills: $hasBills")
        if (hasBills) {
            binding.tvEmptyBills.visibility = android.view.View.GONE
            binding.rvBills.visibility = android.view.View.VISIBLE

            // Show sorting information
            val latestBill = billViewModel.bills.value?.firstOrNull()
            if (latestBill != null) {
                val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(latestBill.timestamp)
                binding.tvEmptyBills.text = "Showing latest bills first\nMost recent: $dateStr"
            }
        } else {
            binding.tvEmptyBills.visibility = android.view.View.VISIBLE
            binding.rvBills.visibility = android.view.View.GONE
            binding.tvEmptyBills.text = "No bills found yet 📝\nGenerate bills from Cart!"
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        Log.d(TAG, "Toast message: $message")
    }
}