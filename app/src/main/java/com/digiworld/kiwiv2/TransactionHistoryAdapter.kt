package com.digiworld.kiwiv2
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionHistoryAdapter(private val mList: List<TransactionHistoryModel>) : RecyclerView.Adapter<TransactionHistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_history_recyclerview, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]

        holder.token.text = ItemsViewModel.token
        holder.amount.text = "%,.2f".format(ItemsViewModel.amount.toFloat())
        holder.datetime.text = ItemsViewModel.datetime

        if (ItemsViewModel.actionType == "100") {
            holder.action.text = "입금"
            holder.amount.setTextColor(Color.parseColor("#800000"))
            holder.amount.text = "+"+holder.amount.text
        } else if (ItemsViewModel.actionType == "101") {
            holder.action.text = "출금"
            holder.amount.setTextColor(Color.parseColor("#000099"))
            holder.amount.text = "-"+holder.amount.text
        } else if (ItemsViewModel.actionType == "200") {
            holder.action.text = "입금대기중"
            holder.amount.setTextColor(Color.parseColor("#800000"))
            holder.amount.text = "+"+holder.amount.text
        } else if (ItemsViewModel.actionType == "201") {
            holder.action.text = "출금대기중"
            holder.amount.setTextColor(Color.parseColor("#000099"))
            holder.amount.text = "-"+holder.amount.text
        } else if (ItemsViewModel.actionType == "300") {
            holder.action.text = "입금 (정기)"
            holder.amount.setTextColor(Color.parseColor("#800000"))
            holder.amount.text = "+"+holder.amount.text
        } else if (ItemsViewModel.actionType == "301") {
            holder.action.text = "송금 (정기)"
            holder.amount.setTextColor(Color.parseColor("#000099"))
            holder.amount.text = "-"+holder.amount.text
        } else if (ItemsViewModel.actionType == "400") {
            holder.action.text = "타인이체"
            holder.amount.setTextColor(Color.parseColor("#000099"))
            holder.amount.text = "-"+holder.amount.text
        } else if (ItemsViewModel.actionType == "401") {
            holder.action.text = "타인이체"
            holder.amount.setTextColor(Color.parseColor("#800000"))
            holder.amount.text = "+"+holder.amount.text
        } else {
            holder.action.text = ItemsViewModel.activityType
            if (ItemsViewModel.amount.toFloat() >= 0) {
                holder.amount.setTextColor(Color.parseColor("#800000"))
                holder.amount.text = "+"+holder.amount.text
            } else {
                holder.amount.setTextColor(Color.parseColor("#000099"))
                holder.amount.text = holder.amount.text
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val datetime: TextView = itemView.findViewById(R.id.datetime)
        val amount: TextView = itemView.findViewById(R.id.amount)
        val action: TextView = itemView.findViewById(R.id.action)
        val token: TextView = itemView.findViewById(R.id.token)
    }
}