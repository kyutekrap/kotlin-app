package com.digiworld.kiwiv2

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.CycleInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class personal_view : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private var transactionHistoryList: ArrayList<TransactionHistoryModel> = ArrayList()
    var username: String = ""
    private lateinit var month1: TextView
    private lateinit var month3: TextView
    private lateinit var month6: TextView
    private lateinit var monthAll: TextView
    private var selectedMonth: Int = 1
    private var receiver_id: String = ""
    private lateinit var noResult: TextView
    private lateinit var loadingLayout: LinearLayout
    private lateinit var loading1: View
    private lateinit var loading2: View
    private lateinit var loading3: View
    private lateinit var loading4: View
    private lateinit var cross: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.personal_view)

        noResult = findViewById(R.id.noResult)
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

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey("receiver_id")) {
                receiver_id = extras.getString("receiver_id").toString()
            }
            if (extras.containsKey("username")) {
                username = extras.getString("username").toString()
            }
        }

        month1 = findViewById(R.id.month1)
        month3 = findViewById(R.id.month3)
        month6 = findViewById(R.id.month6)
        monthAll = findViewById(R.id.monthAll)

        month1.setSafeOnClickListener {
            if (loadingLayout.visibility != View.VISIBLE) {
                if (selectedMonth != 1) {
                    selectedMonth=1
                    month1.background.setTint(ContextCompat.getColor(this, R.color.selected_btn_background))
                    month3.background.setTint(ContextCompat.getColor(this, R.color.unselected_btn_background))
                    month6.background.setTint(ContextCompat.getColor(this, R.color.unselected_btn_background))
                    monthAll.background.setTint(ContextCompat.getColor(this, R.color.unselected_btn_background))
                    month1.setTextColor(ContextCompat.getColor(this, R.color.selected_btn_text))
                    month3.setTextColor(ContextCompat.getColor(this, R.color.unselected_btn_text))
                    month6.setTextColor(ContextCompat.getColor(this, R.color.unselected_btn_text))
                    monthAll.setTextColor(ContextCompat.getColor(this, R.color.unselected_btn_text))
                    transactionHistoryList.clear()
                    loadingLayout.visibility = View.VISIBLE
                    animator1.start(); animator2.start(); animator3.start(); animator4.start()
                    getPersonalHistory()
                    val adapter = TransactionHistoryAdapter(transactionHistoryList)
                    recyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()
                    loadingLayout.visibility = View.GONE
                    animator1.pause(); animator2.pause(); animator3.pause(); animator4.pause()
                }
            }
        }
        month3.setSafeOnClickListener {
            if (loadingLayout.visibility != View.VISIBLE) {
                if (selectedMonth != 3) {
                    selectedMonth = 3
                    month1.background.setTint(ContextCompat.getColor(this, R.color.unselected_btn_background))
                    month3.background.setTint(ContextCompat.getColor(this, R.color.selected_btn_background))
                    month6.background.setTint(ContextCompat.getColor(this, R.color.unselected_btn_background))
                    monthAll.background.setTint(ContextCompat.getColor(this, R.color.unselected_btn_background))
                    month1.setTextColor(ContextCompat.getColor(this, R.color.unselected_btn_text))
                    month3.setTextColor(ContextCompat.getColor(this, R.color.selected_btn_text))
                    month6.setTextColor(ContextCompat.getColor(this, R.color.unselected_btn_text))
                    monthAll.setTextColor(ContextCompat.getColor(this, R.color.unselected_btn_text))
                    transactionHistoryList.clear()
                    loadingLayout.visibility = View.VISIBLE
                    animator1.start(); animator2.start(); animator3.start(); animator4.start()
                    getPersonalHistory()
                    val adapter = TransactionHistoryAdapter(transactionHistoryList)
                    recyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()
                    loadingLayout.visibility = View.GONE
                    animator1.pause(); animator2.pause(); animator3.pause(); animator4.pause()
                }
            }
        }
        month6.setSafeOnClickListener {
            if (loadingLayout.visibility != View.VISIBLE) {
                if (selectedMonth != 6) {
                    selectedMonth = 6
                    month1.background.setTint(ContextCompat.getColor(this, R.color.unselected_btn_background))
                    month3.background.setTint(ContextCompat.getColor(this, R.color.unselected_btn_background))
                    month6.background.setTint(ContextCompat.getColor(this, R.color.selected_btn_background))
                    monthAll.background.setTint(ContextCompat.getColor(this, R.color.unselected_btn_background))
                    month1.setTextColor(ContextCompat.getColor(this, R.color.unselected_btn_text))
                    month3.setTextColor(ContextCompat.getColor(this, R.color.unselected_btn_text))
                    month6.setTextColor(ContextCompat.getColor(this, R.color.selected_btn_text))
                    monthAll.setTextColor(ContextCompat.getColor(this, R.color.unselected_btn_text))
                    transactionHistoryList.clear()
                    loadingLayout.visibility = View.VISIBLE
                    animator1.start(); animator2.start(); animator3.start(); animator4.start()
                    getPersonalHistory()
                    val adapter = TransactionHistoryAdapter(transactionHistoryList)
                    recyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()
                    loadingLayout.visibility = View.GONE
                    animator1.pause(); animator2.pause(); animator3.pause(); animator4.pause()
                }
            }
        }
        monthAll.setSafeOnClickListener {
            if (loadingLayout.visibility != View.VISIBLE) {
                if (selectedMonth != 100) {
                    selectedMonth = 100
                    month1.background.setTint(ContextCompat.getColor(this, R.color.unselected_btn_background))
                    month3.background.setTint(ContextCompat.getColor(this, R.color.unselected_btn_background))
                    month6.background.setTint(ContextCompat.getColor(this, R.color.unselected_btn_background))
                    monthAll.background.setTint(ContextCompat.getColor(this, R.color.selected_btn_background))
                    month1.setTextColor(ContextCompat.getColor(this, R.color.unselected_btn_text))
                    month3.setTextColor(ContextCompat.getColor(this, R.color.unselected_btn_text))
                    month6.setTextColor(ContextCompat.getColor(this, R.color.unselected_btn_text))
                    monthAll.setTextColor(ContextCompat.getColor(this, R.color.selected_btn_text))
                    transactionHistoryList.clear()
                    loadingLayout.visibility = View.VISIBLE
                    animator1.start(); animator2.start(); animator3.start(); animator4.start()
                    getPersonalHistory()
                    val adapter = TransactionHistoryAdapter(transactionHistoryList)
                    recyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()
                    loadingLayout.visibility = View.GONE
                    animator1.pause(); animator2.pause(); animator3.pause(); animator4.pause()
                }
            }
        }

        try {
            loadingLayout.visibility = View.VISIBLE
            animator1.start(); animator2.start(); animator3.start(); animator4.start()
            getPersonalHistory()
            val adapter = TransactionHistoryAdapter(transactionHistoryList)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
            loadingLayout.visibility = View.GONE
            animator1.pause(); animator2.pause(); animator3.pause(); animator4.pause()
        } catch (e: Exception) {
            //
        }
    }

    private fun getPersonalHistory() {
        if (noResult.visibility == View.VISIBLE) {
            noResult.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
        val mURL = URL("https://www.dongkye.tech/A5/getPersonalHistory.php")
        var reqParam = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8")
        reqParam += "&" + URLEncoder.encode("selectedMonth", "UTF-8") + "=" + URLEncoder.encode(selectedMonth.toString(), "UTF-8")
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
                        val tempData = TransactionHistoryModel(
                            baseInfo.getString("activityType"),
                            baseInfo.getString("datetime"),
                            baseInfo.getString("amount"),
                            baseInfo.getString("token"),
                            baseInfo.getString("actionType")
                        )
                        transactionHistoryList.add(tempData)
                    }
                } else {
                    recyclerView.visibility = View.GONE
                    noResult.visibility = View.VISIBLE
                }
            }
        }
    }
}