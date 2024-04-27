package com.digiworld.kiwiv2

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.CycleInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class product_view : AppCompatActivity() {

    private lateinit var fundName: TextView
    private lateinit var seeFakeData: TextView
    private lateinit var total: TextView
    private lateinit var average: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var _newInvestment: TextView
    private var productViewList: ArrayList<ProductViewModel> = ArrayList()
    private var username: String = ""
    private lateinit var loadingLayout: LinearLayout
    private lateinit var loading1: View
    private lateinit var loading2: View
    private lateinit var loading3: View
    private lateinit var loading4: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_view)

        fundName = findViewById(R.id.fundName)
        seeFakeData = findViewById(R.id.seeFakeData)
        total = findViewById(R.id.total)
        average = findViewById(R.id.average)
        _newInvestment = findViewById(R.id.newInvestment)
        loadingLayout = findViewById(R.id.loadinglayout)
        loading1 = findViewById(R.id.loading1)
        loading2 = findViewById(R.id.loading2)
        loading3 = findViewById(R.id.loading3)
        loading4 = findViewById(R.id.loading4)

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

        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey("username")) {
                username = extras.getString("username").toString()
            }
            if (extras.containsKey("fundName")) {
                fundName.text = extras.getString("fundName")
            }
            if (extras.containsKey("investCheck")) {
                if (extras.getString("investCheck") == "true") {
                    _newInvestment.text = "투자상환"
                    _newInvestment.setTextColor(Color.parseColor("#ffff00"))
                } else {
                    if (extras.containsKey("consumedQuota")) {
                        val consumed = intent.getLongExtra("consumedQuota", 0)
                        if (consumed >= 1) {
                            _newInvestment.text = "더 이상 투자할 수 없습니다"
                        }
                    }
                }
            }
        }

        seeFakeData.setOnClickListener {
            startActivity(Intent(this, raw_fake_data::class.java).putExtra("productName", fundName.text.toString().trim()))
        }

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        try {
            loadingLayout.visibility = View.VISIBLE
            animator1.start(); animator2.start(); animator3.start(); animator4.start()
            getProductInfo(fundName.text.toString().trim())
            loadProductView(fundName.text.toString().trim())
            val adapter = ProductViewAdapter(productViewList)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
            loadingLayout.visibility = View.GONE
            animator1.pause(); animator2.pause(); animator3.pause(); animator4.pause()
        } catch (e: Exception) {
            Log.d("Error: ", e.toString())
        }

        _newInvestment.setOnClickListener {
            if (username == "") {
                startActivity(Intent(this, login::class.java))
            } else {
                if (_newInvestment.text.toString().trim() == "신규투자") {
                    startActivity(Intent(this, newInvestment::class.java)
                        .putExtra("productName", fundName.text.toString().trim())
                    )
                } else if (_newInvestment.text.toString().trim() == "투자상환") {
                    startActivity(Intent(this, verifyWithdrawal::class.java).putExtra("productName", fundName.text.toString().trim()))
                }
            }
        }
    }

    private fun getProductInfo(productName: String) {
        val reqParam = URLEncoder.encode("productName", "UTF-8") + "=" + URLEncoder.encode(productName, "UTF-8")
        val mURL = URL("https://www.dongkye.tech/A5/productInfo.php")
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
                val jsonObj = JSONObject(response.toString())
                if (jsonObj.getString("success") == "false") {
                    val intent = Intent(applicationContext, ConnectionError::class.java)
                    startActivity(intent)
                } else if (jsonObj.getString("success") == "true") {
                    total.text = jsonObj.getString("total")
                    average.text = jsonObj.getString("average")
                }
            }
        }
    }

    private fun loadProductView(productName: String) {
        val mURL = URL("https://www.dongkye.tech/A5/productView.php")
        val reqParam = URLEncoder.encode("productName", "UTF-8") + "=" + URLEncoder.encode(productName, "UTF-8")
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
                        val tempData = ProductViewModel(
                            baseInfo.getString("date"),
                            baseInfo.getString("ticker"),
                            baseInfo.getString("pnl"),
                            baseInfo.getString("perc")
                        )
                        productViewList.add(tempData)
                    }
                }
            }
        }
    }
}