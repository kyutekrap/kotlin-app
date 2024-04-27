package com.digiworld.kiwiv2

import android.content.Context
import android.content.Intent
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.text.NumberFormat
import java.util.*

class CurrencyAdapter(private val mList: List<CurrencyModel>, private val context: Context, private val username: String) : RecyclerView.Adapter<CurrencyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.choose_currency_recyclerview, parent, false)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]

        holder.currencyName.text = ItemsViewModel.currencyName

        if (ItemsViewModel.currencyName != "KRW") {
            try {
                getRate(holder.currencyName.text.toString(), holder.currentRate)
            } catch (e: Exception) {
                // Log.d("Error: ", e.toString())
            }
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, change_baseCurrency::class.java)
            intent.putExtra("baseCurrency", holder.currencyName.text.toString())
            intent.putExtra("username", username)
            context.startActivity(intent)
        }
    }

    private fun getRate(currencyName: String, currentRate: TextView) {
        val connection = URL("https://api.upbit.com/v1/ticker?markets=KRW-$currencyName").openConnection() as HttpURLConnection
        val data = connection.inputStream.bufferedReader().readText()
        val jsonArray = JSONArray(data)
        if (jsonArray.length() > 0) {
            val number = NumberFormat.getNumberInstance(Locale.US).format(jsonArray.getJSONObject(0).get("trade_price").toString().toFloat())
            currentRate.text = "≈" + number + "원"
        } else {
            currentRate.text = "시세를 알 수 없음"
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val currencyName: TextView = itemView.findViewById(R.id.currencyName)
        val currentRate: TextView = itemView.findViewById(R.id.currentRate)
    }
}