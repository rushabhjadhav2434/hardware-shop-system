package com.yourcompany.hardwareshop.auth

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yourcompany.hardwareshop.data.model.Bill
import com.yourcompany.hardwareshop.databinding.ItemBillBinding
import java.text.SimpleDateFormat
import java.util.*

class BillAdapter(
    private var bills: List<Bill> = emptyList()
) : RecyclerView.Adapter<BillAdapter.BillViewHolder>() {

    inner class BillViewHolder(private val binding: ItemBillBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(bill: Bill) {
            val dateStr = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(bill.timestamp)

            // Simple display - latest bill first
            binding.tvBillNumber.text = "Bill #${adapterPosition + 1}"
            binding.tvBillDate.text = "Date: $dateStr"
            binding.tvBillTotal.text = "₹${bill.totalAmount}"
            binding.tvBillStatus.text = bill.status
            binding.tvItemCount.text = "Items: ${bill.items.size}"

            // Hide latest indicator for now
            binding.tvLatestIndicator.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        val binding = ItemBillBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BillViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        holder.bind(bills[position])
    }

    override fun getItemCount() = bills.size

    fun updateBills(newBills: List<Bill>) {
        bills = newBills
        notifyDataSetChanged()
    }
}