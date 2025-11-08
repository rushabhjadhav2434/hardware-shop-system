package com.yourcompany.hardwareshop.owner

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.yourcompany.hardwareshop.owner.databinding.ActivityOwnerBillsBinding
import java.text.SimpleDateFormat
import java.util.*

class OwnerBillsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOwnerBillsBinding
    private val db = FirebaseFirestore.getInstance()
    private val billsCollection = db.collection("bills")
    private val billList = mutableListOf<Bill>()
    private val allBills = mutableListOf<Bill>()
    private lateinit var adapter: BillAdapter

    private var selectedStartDate: Date? = null
    private var selectedEndDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOwnerBillsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupClickListeners()
        loadAllBills()
    }

    private fun setupRecyclerView() {
        adapter = BillAdapter(billList) { bill ->
            showBillDetailsDialog(bill)
        }
        binding.rvBills.layoutManager = LinearLayoutManager(this)
        binding.rvBills.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.btnFilter.setOnClickListener {
            showDateFilterDialog()
        }

        binding.btnClearFilter.setOnClickListener {
            clearFilter()
        }
    }

    private fun loadAllBills() {
        billsCollection.orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                allBills.clear()
                billList.clear()

                for (document in querySnapshot) {
                    val bill = Bill(
                        id = document.id,
                        userId = document.getString("userId") ?: "",
                        userName = document.getString("userName") ?: "Unknown",
                        items = emptyList(),
                        totalAmount = document.getDouble("totalAmount") ?: 0.0,
                        timestamp = document.getDate("timestamp") ?: Date(),
                        status = document.getString("status") ?: "Pending"
                    )
                    allBills.add(bill)
                }

                // Show all bills initially
                billList.addAll(allBills)
                adapter.notifyDataSetChanged()
                updateUI()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading bills: ${e.message}", Toast.LENGTH_SHORT).show()
                updateUI()
            }
    }

    private fun showDateFilterDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Start Date Picker
        DatePickerDialog(this, { _, startYear, startMonth, startDay ->
            selectedStartDate = getDateFromParts(startYear, startMonth, startDay)

            // End Date Picker
            DatePickerDialog(this, { _, endYear, endMonth, endDay ->
                selectedEndDate = getDateFromParts(endYear, endMonth, endDay)

                // Apply filter
                applyDateFilter()

            }, year, month, day).show()

        }, year, month, day).show()
    }

    private fun getDateFromParts(year: Int, month: Int, day: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    private fun applyDateFilter() {
        if (selectedStartDate == null || selectedEndDate == null) {
            Toast.makeText(this, "Please select both start and end dates", Toast.LENGTH_SHORT).show()
            return
        }

        // Ensure end date is at the end of the day
        val endDate = Calendar.getInstance().apply {
            time = selectedEndDate!!
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }.time

        billList.clear()

        val filteredBills = allBills.filter { bill ->
            bill.timestamp.after(selectedStartDate) && bill.timestamp.before(endDate)
        }

        billList.addAll(filteredBills)
        adapter.notifyDataSetChanged()
        updateFilterInfo()
        updateUI()
    }

    private fun clearFilter() {
        selectedStartDate = null
        selectedEndDate = null
        billList.clear()
        billList.addAll(allBills)
        adapter.notifyDataSetChanged()
        updateFilterInfo()
        updateUI()
        Toast.makeText(this, "Filter cleared", Toast.LENGTH_SHORT).show()
    }

    private fun updateFilterInfo() {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        if (selectedStartDate != null && selectedEndDate != null) {
            val startStr = dateFormat.format(selectedStartDate!!)
            val endStr = dateFormat.format(selectedEndDate!!)
            val count = billList.size
            binding.tvFilterInfo.text = "Showing $count bills from $startStr to $endStr"
        } else {
            binding.tvFilterInfo.text = "Showing all ${billList.size} bills"
        }
    }

    private fun updateUI() {
        if (billList.isEmpty()) {
            binding.tvEmpty.visibility = android.view.View.VISIBLE
            binding.rvBills.visibility = android.view.View.GONE
        } else {
            binding.tvEmpty.visibility = android.view.View.GONE
            binding.rvBills.visibility = android.view.View.VISIBLE
        }
        updateFilterInfo()
    }

    private fun showBillDetailsDialog(bill: Bill) {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Bill Details - ${bill.userName}")

        // Get full bill details with items
        billsCollection.document(bill.id).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val items = document.get("items") as? List<Map<String, Any>> ?: emptyList()

                    val message = StringBuilder()
                    message.append("Bill ID: ${bill.id}\n")
                    message.append("Customer: ${bill.userName}\n")
                    message.append("Date: ${SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(bill.timestamp)}\n")
                    message.append("Status: ${bill.status}\n\n")
                    message.append("Items:\n")

                    items.forEachIndexed { index, item ->
                        val name = item["name"] as? String ?: "Unknown"
                        val quantity = (item["quantity"] as? Long ?: 0).toInt()
                        val price = item["price"] as? Double ?: 0.0
                        val total = quantity * price
                        message.append("${index + 1}. $name x$quantity = ₹${"%.2f".format(total)}\n")
                    }

                    message.append("\nTotal Amount: ₹${"%.2f".format(bill.totalAmount)}")

                    builder.setMessage(message.toString())

                    // Add status update buttons
                    if (bill.status == "Pending") {
                        builder.setPositiveButton("Mark Completed") { dialog, which ->
                            updateBillStatus(bill.id, "Completed")
                        }
                    }

                    builder.setNegativeButton("Close", null)
                    builder.show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading bill details: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateBillStatus(billId: String, newStatus: String) {
        billsCollection.document(billId)
            .update("status", newStatus)
            .addOnSuccessListener {
                Toast.makeText(this, "Bill marked as $newStatus", Toast.LENGTH_SHORT).show()
                loadAllBills() // Reload all bills
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error updating bill: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

data class Bill(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val items: List<Any> = emptyList(),
    val totalAmount: Double = 0.0,
    val timestamp: Date = Date(),
    val status: String = "Pending"
)

class BillAdapter(
    private val bills: List<Bill>,
    private val onItemClick: (Bill) -> Unit
) : androidx.recyclerview.widget.RecyclerView.Adapter<BillAdapter.ViewHolder>() {

    class ViewHolder(val view: android.view.View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val textView: android.widget.TextView = view.findViewById(android.R.id.text1)
        val textView2: android.widget.TextView = view.findViewById(android.R.id.text2)
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bill = bills[position]
        val dateStr = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(bill.timestamp)

        holder.textView.text = "${bill.userName} - ₹${"%.2f".format(bill.totalAmount)}"
        holder.textView2.text = "Date: $dateStr | Status: ${bill.status}"

        // Color code based on status
        when (bill.status) {
            "Completed" -> {
                holder.textView.setTextColor(0xFF4CAF50.toInt())
                holder.textView2.setTextColor(0xFF4CAF50.toInt())
            }
            "Pending" -> {
                holder.textView.setTextColor(0xFFFF9800.toInt())
                holder.textView2.setTextColor(0xFFFF9800.toInt())
            }
            else -> {
                holder.textView.setTextColor(0xFF000000.toInt())
                holder.textView2.setTextColor(0xFF000000.toInt())
            }
        }

        holder.view.setOnClickListener {
            onItemClick(bill)
        }
    }

    override fun getItemCount() = bills.size
}