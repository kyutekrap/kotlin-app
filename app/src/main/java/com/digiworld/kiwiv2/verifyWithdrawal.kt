package com.digiworld.kiwiv2

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.CycleInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.ObjectStreamException
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class verifyWithdrawal : AppCompatActivity() {

    private lateinit var productName: TextView
    private lateinit var investAmount: TextView
    private lateinit var error_msg: TextView
    private lateinit var savebtn: TextView
    private lateinit var loadingLayout: LinearLayout
    private lateinit var loading1: View
    private lateinit var loading2: View
    private lateinit var loading3: View
    private lateinit var loading4: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_withdrawal)

        productName = findViewById(R.id.productName)
        investAmount = findViewById(R.id.investAmount)
        error_msg = findViewById(R.id.error_msg)
        savebtn = findViewById(R.id.savebtn)
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

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val sharedPreference =  getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val username = sharedPreference.getString("username", "").toString()

        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey("productName")) {
                productName.text = extras.getString("productName")
            }
        }

        try {
            loadingLayout.visibility = View.VISIBLE
            animator1.start(); animator2.start(); animator3.start(); animator4.start()
            initialInvestment(username, productName.text.toString())
            loadingLayout.visibility = View.GONE
            animator1.pause(); animator2.pause(); animator3.pause(); animator4.pause()
        } catch (e: Exception) {
            Log.d("Error: ", e.toString())
        }

        savebtn.setSafeOnClickListener {
            if (loadingLayout.visibility != View.VISIBLE) {
                try {
                    loadingLayout.visibility = View.VISIBLE
                    animator1.start(); animator2.start(); animator3.start(); animator4.start()
                    withdrawFromFund(
                        username,
                        productName.text.toString(),
                        animator1,
                        animator2,
                        animator3,
                        animator4
                    )
                } catch (e: Exception) {
                    // Log.d("Error: ", e.toString())
                }
            }
        }
    }

    private fun withdrawFromFund(username: String, product_name: String, animator1: ObjectAnimator, animator2: ObjectAnimator, animator3: ObjectAnimator, animator4: ObjectAnimator) {
        var reqParam = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8")
        reqParam += "&" + URLEncoder.encode("productName", "UTF-8") + "=" + URLEncoder.encode(product_name, "UTF-8")
        val mURL = URL("https://www.dongkye.tech/A5/withdrawFromFund.php")
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
                    error_msg.visibility = View.VISIBLE
                    loadingLayout.visibility = View.GONE
                    animator1.pause(); animator2.pause(); animator3.pause(); animator4.pause()
                } else if (jsonObj.getString("success") == "true") {
                    startActivity(Intent(applicationContext, MainParent::class.java).putExtra("context", "main"))
                }
            }
        }
    }

    private fun initialInvestment(username: String, product_name: String) {
        var reqParam = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8")
        reqParam += "&" + URLEncoder.encode("productName", "UTF-8") + "=" + URLEncoder.encode(product_name, "UTF-8")
        val mURL = URL("https://www.dongkye.tech/A5/getInitialAmount.php")
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
                    error_msg.visibility = View.VISIBLE
                    savebtn.visibility = View.GONE
                } else if (jsonObj.getString("success") == "true") {
                    investAmount.text = "%,.2f".format(jsonObj.getString("initialInvestment").toFloat())
                }
            }
        }
    }

    fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }
}