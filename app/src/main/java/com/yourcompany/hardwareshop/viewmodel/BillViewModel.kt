package com.yourcompany.hardwareshop.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourcompany.hardwareshop.data.model.Bill
import com.yourcompany.hardwareshop.data.repository.BillRepository
import kotlinx.coroutines.launch

class BillViewModel(private val billRepository: BillRepository) : ViewModel() {

    private val _bills = MutableLiveData<List<Bill>>()
    val bills: LiveData<List<Bill>> get() = _bills

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _billGenerated = MutableLiveData<Boolean>()
    val billGenerated: LiveData<Boolean> get() = _billGenerated

    fun generateBill(cartItems: List<com.yourcompany.hardwareshop.data.model.CartItem>, totalAmount: Double) {
        _loading.value = true
        viewModelScope.launch {
            try {
                val result = billRepository.generateBill(cartItems, totalAmount)
                if (result.isSuccess) {
                    _billGenerated.value = true
                    _error.value = ""
                } else {
                    _error.value = "Failed to generate bill: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _error.value = "Error generating bill: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadUserBills(userId: String) {
        _loading.value = true
        viewModelScope.launch {
            try {
                val billList = billRepository.getBillsByUser(userId)
                _bills.value = billList
                _error.value = ""
            } catch (e: Exception) {
                _error.value = "Failed to load bills: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadAllBills() {
        _loading.value = true
        viewModelScope.launch {
            try {
                val billList = billRepository.getAllBills()
                _bills.value = billList
                _error.value = ""
            } catch (e: Exception) {
                _error.value = "Failed to load bills: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateBillStatus(billId: String, status: String) {
        viewModelScope.launch {
            try {
                billRepository.updateBillStatus(billId, status)
                // Reload bills after status update
                loadAllBills()
            } catch (e: Exception) {
                _error.value = "Failed to update bill status: ${e.message}"
            }
        }
    }
}