package com.digiworld.kiwiv2

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductViewAdapter(private val mList: List<ProductViewModel>) : RecyclerView.Adapter<ProductViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.personal_product_view_recyclerview, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]

        holder.date.text = ItemsViewModel.date
        holder.ticker.text = ItemsViewModel.ticker
        holder.pnl.text = ItemsViewModel.pnl
        holder.perc.text = ItemsViewModel.perc + "%"

        if (ItemsViewModel.perc.toFloat() >= 0) {
            holder.perc.setTextColor(Color.parseColor("#800000"))
        } else {
            holder.perc.setTextColor(Color.parseColor("#000099"))
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val date: TextView = itemView.findViewById(R.id.date)
        val ticker: TextView = itemView.findViewById(R.id.ticker)
        val pnl: TextView = itemView.findViewById(R.id.pnl)
        val perc: TextView = itemView.findViewById(R.id.perc)
    }
}