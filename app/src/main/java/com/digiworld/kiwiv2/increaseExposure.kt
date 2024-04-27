package com.digiworld.kiwiv2

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.CycleInterpolator
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class increaseExposure : AppCompatActivity() {

    private lateinit var productName: TextView
    private lateinit var amount: EditText
    private lateinit var error_msg: TextView
    private lateinit var total: TextView
    private var initial_amount: Float = 0.0f
    private lateinit var initialAmount: TextView
    private lateinit var savebtn: TextView
    private var username: String = ""
    private lateinit var loadingLayout: LinearLayout
    private lateinit var loading1: View
    private lateinit var loading2: View
    private lateinit var loading3: View
    private lateinit var loading4: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_increase_exposure)

        productName = findViewById(R.id.productName)
        amount = findViewById(R.id.amount)
        error_msg = findViewById(R.id.error_msg)
        total = findViewById(R.id.total)
        initialAmount = findViewById(R.id.initialAmount)
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

        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey("productName")) {
                productName.text = extras.getString("productName")
            }
            if (extras.containsKey("username")) {
                username = extras.getString("username").toString()
            }
        }

        try {
            loadingLayout.visibility = View.VISIBLE
            animator1.start(); animator2.start(); animator3.start(); animator4.start()
            getInitialAmount(username, productName.text.toString().trim())
            animator1.pause(); animator2.pause(); animator3.pause(); animator4.pause()
            loadingLayout.visibility = View.GONE
        } catch (e: Exception) {
            Log.d("Error: ", e.toString())
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }
            override fun afterTextChanged(p0: Editable?) {
                val amount = p0.toString().toFloat()
                total.text = "%,.2f".format(initial_amount + amount)
            }
        }
        amount.addTextChangedListener(textWatcher)

        savebtn.setSafeOnClickListener {
            if (loadingLayout.visibility != View.VISIBLE) {
                var stop = 0
                if (amount.text.toString().trim() != "") {
                    if (amount.text.toString().toFloat() > 0) {
                        if (amount.text.toString().contains(".")) {
                            if (amount.text.toString().split(".")[1].length > 2) {
                                amount.error = "소수점 2자리까지만 반영됩니다"
                                stop = 1
                            }
                        }
                    }
                } else {
                    stop = 1
                }
                if (stop == 0) {
                    try {
                        loadingLayout.visibility = View.VISIBLE
                        animator1.start(); animator2.start(); animator3.start(); animator4.start()
                        increase_exposure(
                            username,
                            total.text.toString().trim(),
                            productName.text.toString().trim(),
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
    }

    private fun increase_exposure(username: String, _amount: String, productName: String, animator1: ObjectAnimator, animator2: ObjectAnimator, animator3: ObjectAnimator, animator4: ObjectAnimator) {
        var reqParam = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8")
        reqParam += "&" + URLEncoder.encode("total", "UTF-8") + "=" + URLEncoder.encode(_amount, "UTF-8")
        reqParam += "&" + URLEncoder.encode("productName", "UTF-8") + "=" + URLEncoder.encode(productName, "UTF-8")
        reqParam += "&" + URLEncoder.encode("increasedAmt", "UTF-8") + "=" + URLEncoder.encode(amount.text.toString(), "UTF-8")
        val mURL = URL("https://www.dongkye.tech/A5/increase_exposure.php")
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
                    val intent = Intent(applicationContext, MainParent::class.java)
                    intent.putExtra("productName", productName)
                    intent.putExtra("context", "productChanged")
                    startActivity(intent)
                } else {
                    loadingLayout.visibility = View.GONE
                    if (jsonObj.getString("success") == "false") {
                        error_msg.visibility = View.VISIBLE
                    } else if (jsonObj.getString("success") == "insufficient") {
                        amount.error = "증액분이 보유액보다 많습니다"
                    }
                    animator1.pause(); animator2.pause(); animator3.pause(); animator4.pause()
                }
            }
        }
    }

    private fun getInitialAmount(username:String, productName:String) {
        var reqParam = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8")
        reqParam += "&" + URLEncoder.encode("productName", "UTF-8") + "=" + URLEncoder.encode(productName, "UTF-8")
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
                    val intent = Intent(applicationContext, ConnectionError::class.java)
                    startActivity(intent)
                } else if (jsonObj.getString("success") == "true") {
                    initial_amount = jsonObj.getString("initialInvestment").toFloat()
                    initialAmount.text = "%,.2f".format(jsonObj.getString("initialInvestment").toFloat())
                    total.text = "%,.2f".format(jsonObj.getString("initialInvestment").toFloat())
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