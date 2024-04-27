package com.digiworld.kiwiv2

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.CycleInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class personal_transaction_history : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private var transactionList: ArrayList<RecipientFindModel> = ArrayList()
    private lateinit var noRecord: TextView
    private var username: String = ""
    private var receiver_id: String = ""
    private lateinit var loadingLayout: LinearLayout
    private lateinit var loading1: View
    private lateinit var loading2: View
    private lateinit var loading3: View
    private lateinit var loading4: View
    private lateinit var cross: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.personal_transaction_history)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        noRecord = findViewById(R.id.noRecord)
        loadingLayout = findViewById(R.id.loadinglayout)
        loading1 = findViewById(R.id.loading1)
        loading2 = findViewById(R.id.loading2)
        loading3 = findViewById(R.id.loading3)
        loading4 = findViewById(R.id.loading4)
        cross = findViewById(R.id.cross)

        cross.setOnClickListener {
            finish()
        }

        val animator1 = ObjectAnimator.ofFloat(loading1, "translationY", 50f)
        animator1.interpolator = CycleInterpolator(1f)
        animator1.duration = 2000

        val animator2 = ObjectAnimator.ofFloat(loading2, "translationY", 50f)
        animator2.interpolator = CycleInterpolator(1f)
        animator2.duration = 2000
        animator2.startDelay = 200

        val animator3 = ObjectAnimator.ofFloat(loading3, "translationY", 50f)
        animator3.interpolator = CycleInterpolator(1f)
        animator3.duration = 2000
        animator3.startDelay = 400

        val animator4 = ObjectAnimator.ofFloat(loading4, "translationY", 50f)
        animator4.interpolator = CycleInterpolator(1f)
        animator4.duration = 2000
        animator4.startDelay = 600

        animator1.repeatCount = Animation.INFINITE
        animator2.repeatCount = Animation.INFINITE
        animator3.repeatCount = Animation.INFINITE
        animator4.repeatCount = Animation.INFINITE

        animator1.repeatMode = ValueAnimator.RESTART
        animator2.repeatMode = ValueAnimator.RESTART
        animator3.repeatMode = ValueAnimator.RESTART
        animator4.repeatMode = ValueAnimator.RESTART

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey("username")) {
                username = extras.getString("username").toString()
            }
            if (extras.containsKey("receiver_id")) {
                receiver_id = extras.getString("receiver_id").toString()
            }
        }

        try {
            loadingLayout.visibility = View.VISIBLE
            animator1.start(); animator2.start(); animator3.start(); animator4.start()
            loadHistory()
            val adapter = RecipientFindAdapter(transactionList)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
            loadingLayout.visibility = View.GONE
            animator1.pause(); animator2.pause(); animator3.pause(); animator4.pause()
        } catch (e: Exception) {
            // Log.d("Error: ", e.toString())
        }
    }

    private fun loadHistory() {
        val mURL = URL("https://www.dongkye.tech/A5/personal_transaction_history.php")
        var reqParam = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8")
        reqParam += "&" + URLEncoder.encode("receiver_id", "UTF-8") + "=" + URLEncoder.encode(receiver_id, "UTF-8")
        with(mURL.openConnection() as HttpURLConnection) {
            requestMethod = "POST"
            val wr = OutputStreamWriter(outputStream)
            wr.write(reqParam)
            wr.flush()
            BufferedReader(InputStreamReader(inputStream)).use {
                val response = StringBuffer()
                var inputLine = it.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }
                val jsonArray = JSONArray(response.toString())
                if (jsonArray.length() > 0) {
                    for (i in 0 until jsonArray.length()) {
                        val baseInfo = jsonArray.getJSONObject(i)
                        val tempData = RecipientFindModel(
                            baseInfo.getString("datetime"),
                            baseInfo.getString("actual_amount"),
                            baseInfo.getString("token"),
                            baseInfo.getString("recipient"),
                            baseInfo.getString("toAddress"),
                            baseInfo.getString("action")
                        )
                        transactionList.add(tempData)
                    }
                } else {
                    noRecord.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
            }
        }
    }
    inner class RecipientFindAdapter(private val mList: ArrayList<RecipientFindModel>) : RecyclerView.Adapter<RecipientFindAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.personal_transaction_history_recyclerview, parent, false)

            return ViewHolder(view)
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val ItemsViewModel = mList[position]

            holder.datetime.text = ItemsViewModel.datetime
            holder.address.text = ItemsViewModel.recipient + " : " + ItemsViewModel.toAddress
            if (ItemsViewModel.action == "1") {
                holder.amount.text = "+" + ItemsViewModel.actual_amount
                holder.amount.setTextColor(Color.parseColor("#800000"))
            } else {
                holder.amount.text = "-" + ItemsViewModel.actual_amount
                holder.amount.setTextColor(Color.parseColor("#000099"))
            }
            holder.token.text = ItemsViewModel.token
        }

        override fun getItemCount(): Int {
            return mList.size
        }

        inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
            val datetime: TextView = itemView.findViewById(R.id.datetime)
            val amount: TextView = itemView.findViewById(R.id.amount)
            val address: TextView = itemView.findViewById(R.id.timeonly)
            val token: TextView = itemView.findViewById(R.id.token)
        }
    }
}