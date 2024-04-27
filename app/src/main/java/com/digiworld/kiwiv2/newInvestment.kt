package com.digiworld.kiwiv2

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.view.animation.Animation
import android.view.animation.CycleInterpolator
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class newInvestment : AppCompatActivity() {

    private lateinit var productName: TextView
    private lateinit var investAmount: EditText
    private lateinit var savebtn: TextView
    private lateinit var error_msg: TextView
    private lateinit var loadingLayout: LinearLayout
    private lateinit var loading1: View
    private lateinit var loading2: View
    private lateinit var loading3: View
    private lateinit var loading4: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_investment)

        productName = findViewById(R.id.productName)
        investAmount = findViewById(R.id.investAmount)
        savebtn = findViewById(R.id.savebtn)
        error_msg = findViewById(R.id.error_msg)
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

        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey("productName")) {
                productName.text = extras.getString("productName")
            }
        }

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val sharedPreference =  getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val username = if (sharedPreference.contains("username")) {
            sharedPreference.getString("username", "default").toString()
        } else {
            ""
        }

        investAmount.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                investAmount.background = ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.login_form_edittext_focused
                )
            } else {
                investAmount.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.login_form_edittext)
            }
        }

        savebtn.setSafeOnClickListener {
            if (loadingLayout.visibility != View.VISIBLE) {
                var stop = 0
                val investamount_txt = investAmount.text.toString()
                if (investamount_txt != "") {
                    if (investamount_txt.contains(".")) {
                        if (investamount_txt.split(".")[1].length > 6) {
                            investAmount.error = "소수점 6자리까지만 반영됩니다"
                            stop = 1
                        }
                    }
                    if (investamount_txt.toFloat() == 0F) {
                        stop = 1
                    }
                }
                if (stop == 0) {
                    loadingLayout.visibility = View.VISIBLE
                    animator1.start(); animator2.start(); animator3.start(); animator4.start()
                    new_investment(
                        username,
                        productName.text.toString(),
                        investamount_txt,
                        animator1,
                        animator2,
                        animator3,
                        animator4
                    )
                }
            }
        }
    }

    private fun new_investment(username: String, productName: String, investamountTxt: String, animator1: ObjectAnimator, animator2: ObjectAnimator, animator3: ObjectAnimator, animator4: ObjectAnimator) {
        var reqParam = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8")
        reqParam += "&" + URLEncoder.encode("productName", "UTF-8") + "=" + URLEncoder.encode(productName, "UTF-8")
        reqParam += "&" + URLEncoder.encode("investamountTxt", "UTF-8") + "=" + URLEncoder.encode(investamountTxt, "UTF-8")
        val mURL = URL("https://www.dongkye.tech/A5/newInvestment.php")
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
                if (jsonObj.getString("success") == "true") {
                    startActivity(Intent(applicationContext, MainParent::class.java)
                        .putExtra("context", "main")
                    )
                } else {
                    loadingLayout.visibility = View.GONE
                    if (jsonObj.getString("success") == "false") {
                        error_msg.text = "네트워크 연결이 불안정합니다"
                        error_msg.visibility = View.VISIBLE
                    } else if (jsonObj.getString("success") == "insufficient") {
                        error_msg.text = "자산이 부족합니다"
                        error_msg.visibility = View.VISIBLE
                    }
                    animator1.pause(); animator2.pause(); animator3.pause(); animator4.pause()
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