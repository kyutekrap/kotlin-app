package com.digiworld.kiwiv2

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class manage_subscribed_payments_history_Adapter(private val mList: List<ManageSubscribedPaymentsHistoryModel>) : RecyclerView.Adapter<manage_subscribed_payments_history_Adapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.manage_subscribed_payments_history_recyclerview, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]

        holder.date.text = ItemsViewModel.date
        if (ItemsViewModel.result == "paid") {
            holder.result.text = "완료"
        } else {
            holder.result.text = "예정"
            holder.result.setTextColor(Color.parseColor("#800000"))
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val date: TextView = itemView.findViewById(R.id.date)
        val result: TextView = itemView.findViewById(R.id.result)
    }
}