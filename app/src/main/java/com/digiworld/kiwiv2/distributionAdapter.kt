package com.digiworld.kiwiv2

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class distributionAdapter(
    private val mList: List<distributionModel>,
    private val context: Context,
    private val username: String
) : RecyclerView.Adapter<distributionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.main_recyclerview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]
        holder.actualAmount.text = ItemsViewModel.perc
        holder.serviceName.text = ItemsViewModel.serviceName
        holder.distribution.text = ItemsViewModel.distributedAmount + "%"
        holder.itemView.setOnClickListener {
            val intent = Intent(context, personal_product_view::class.java)
            intent.putExtra("productName", holder.serviceName.text.toString())
            intent.putExtra("username", username)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val serviceName: TextView = itemView.findViewById(R.id.serviceName)
        val distribution: TextView = itemView.findViewById(R.id.distribution)
        val actualAmount: TextView = itemView.findViewById(R.id.actualAmount)
    }
}