package com.digiworld.kiwiv2

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView

class manage_subscribedPayments_Adapter(private val mList: List<ManageSubscribedPaymentsModel>, private val context: Context, private val manage: String) : RecyclerView.Adapter<manage_subscribedPayments_Adapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.subscribed_payments_recyclerview, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]

        holder.paymentName.text = ItemsViewModel.paymentName
        holder.currency.text = ItemsViewModel.token

        if (ItemsViewModel.token == "KRW") {
            holder.amount.text = "%,.2f".format(ItemsViewModel.amount.toFloat())
        } else {
            holder.amount.text = "%,.6f".format(ItemsViewModel.amount.toFloat())
        }

        holder.date.text = "매월 " + ItemsViewModel.payment_date + "일"
        if (manage == "") { // 수취인
            holder.recipient.text = ItemsViewModel.recipient
            holder.toAddress.text = ItemsViewModel.recipient_address
            val intent = Intent(context, subscribed_payments_history::class.java)
            intent.putExtra("paymentName", holder.paymentName.text.toString())
            intent.putExtra("amount", holder.amount.text.toString())
            intent.putExtra("currency", holder.currency.text.toString())
            intent.putExtra("recipient", ItemsViewModel.recipient)
            intent.putExtra("address", ItemsViewModel.recipient_address)
            intent.putExtra("paymentId", ItemsViewModel.paymentId)
            intent.putExtra("payment_date", holder.date.text.toString())
            holder.itemView.setOnClickListener {
                context.startActivity(intent)
            }
        } else {
            holder.recipient.text = ItemsViewModel.recipient
            holder.toAddress.text = ItemsViewModel.recipient_address
            val intent = Intent(context, manage_subscribed_payments_history::class.java)
            intent.putExtra("paymentName", holder.paymentName.text.toString())
            intent.putExtra("amount", holder.amount.text.toString())
            intent.putExtra("currency", holder.currency.text.toString())
            intent.putExtra("recipient", ItemsViewModel.recipient)
            intent.putExtra("address", ItemsViewModel.recipient_address)
            intent.putExtra("paymentId", ItemsViewModel.paymentId)
            intent.putExtra("payment_date", holder.date.text.toString())
            holder.itemView.setOnClickListener {
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val paymentName: TextView = itemView.findViewById(R.id.paymentName)
        val amount: TextView = itemView.findViewById(R.id.amount)
        val date: TextView = itemView.findViewById(R.id.date)
        val currency: TextView = itemView.findViewById(R.id.currency)
        val recipient: TextView = itemView.findViewById(R.id.recipient)
        val toAddress: TextView = itemView.findViewById(R.id.toAddress)
    }
}