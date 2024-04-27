package com.digiworld.kiwiv2

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.Animation
import android.view.animation.CycleInterpolator
import android.widget.*
import androidx.core.content.ContextCompat
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class subscribed_payments_add : AppCompatActivity() {

    private lateinit var displayName: EditText
    private lateinit var dateOnly: EditText
    private lateinit var startTomorrow: CheckBox
    private lateinit var amount: EditText
    private lateinit var result_msg: TextView
    private lateinit var savebtn: TextView
    private var baseCurrency: String = ""
    private var username: String = ""
    private var address: String = ""
    private var receiver_id: String = ""
    private lateinit var paymentName: EditText
    private var user_nickname: String = ""
    private lateinit var cross: ImageView
    private lateinit var loadingLayout: LinearLayout
    private lateinit var loading1: View
    private lateinit var loading2: View
    private lateinit var loading3: View
    private lateinit var loading4: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscribed_payments_add)

        displayName = findViewById(R.id.displayName)
        dateOnly = findViewById(R.id.date)
        startTomorrow = findViewById(R.id.startTomorrow)
        amount = findViewById(R.id.amount)
        result_msg = findViewById(R.id.result_msg)
        savebtn = findViewById(R.id.savebtn)
        paymentName = findViewById(R.id.paymentName)
        cross = findViewById(R.id.cross)
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

        cross.setOnClickListener {
            finish()
        }

        val sharedPreference =  getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        username = sharedPreference.getString("username", "").toString()

        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey("recipient")) {
                displayName.setText(extras.getString("recipient"))
            }
            if (extras.containsKey("user_nickname")) {
                user_nickname = extras.getString("user_nickname").toString()
            }
            if (extras.containsKey("date")) {
                dateOnly.setText(extras.getString("date"))
            }
            if (extras.containsKey("startTomorrow")) {
                if (extras.getBoolean("startTomorrow")) {
                    startTomorrow.isChecked = true
                }
            }
            if (extras.containsKey("amount")) {
                amount.setText(extras.getString("amount"))
            }
            if (extras.containsKey("baseCurrency")) {
                if (extras.getString("baseCurrency").toString() == "") {
                    getBaseCurrency()
                } else {
                    baseCurrency = extras.getString("baseCurrency").toString()
                }
            }
            if (extras.containsKey("address")) {
                address = extras.getString("address").toString()
            }
            if (extras.containsKey("receiver_id")) {
                receiver_id = extras.getString("receiver_id").toString()
            }
            if (extras.containsKey("paymentName")) {
                paymentName.setText(extras.getString("paymentName"))
            }
        }

        paymentName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                paymentName.background = ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.login_form_edittext_focused
                )
            } else {
                paymentName.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.login_form_edittext)
            }
        }
        dateOnly.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                dateOnly.background = ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.login_form_edittext_focused
                )
            } else {
                dateOnly.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.login_form_edittext)
            }
        }

        dateOnly.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //
            }
            override fun afterTextChanged(s: Editable?) {
                try {
                    val value = s.toString().toInt()
                    if (value > 31) {
                        s?.replace(0, s.length, "31")
                    }
                } catch (e: NumberFormatException) {
                    // Ignore non-numeric input
                }
            }
        })

        amount.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                amount.background = ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.login_form_edittext_focused
                )
            } else {
                amount.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.login_form_edittext)
            }
        }

        if (displayName.text.toString() == "") {
            displayName.setBackgroundResource(R.drawable.login_form_edittext)
        } else {
            displayName.setBackgroundResource(R.drawable.login_form_edittext_focused)
        }

        displayName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                startActivity(Intent(this, subscribed_payments_find::class.java)
                    .putExtra("username", username)
                    .putExtra("baseCurrency", baseCurrency)
                    .putExtra("date", dateOnly.text.toString())
                    .putExtra("startTomorrow", startTomorrow.isChecked)
                    .putExtra("amount", amount.text.toString())
                    .putExtra("recipient", displayName.text.toString())
                    .putExtra("address", address)
                    .putExtra("receiver_id", receiver_id)
                    .putExtra("paymentName", paymentName.text.toString())
                    .putExtra("user_nickname", user_nickname)
                )
            }
        }

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        savebtn.setSafeOnClickListener {
            if (loadingLayout.visibility != View.VISIBLE) {
                loadingLayout.visibility = View.VISIBLE
                animator1.start(); animator2.start(); animator3.start(); animator4.start()
                var stop=0
                if (displayName.text.toString() != "" && dateOnly.text.toString() != "" && amount.text.toString() != "") {
                    if (amount.text.contains(".")) {
                        if (amount.text.split(".")[1].length > 6) {
                            amount.error = "소수점 6자리까지 지원됩니다"
                            stop=1
                        }
                    }
                    if (amount.text.toString().toFloat() == 0F) {
                        amount.error = "약정 금액은 0보다 많아야 합니다"
                        stop=1
                    }
                    if (address == receiver_id) {
                        displayName.error = "자신에게 송금할 수 없습니다"
                        stop=1
                    }
                } else {
                    stop=1
                }
                if (stop==0) {
                    addPayment()
                }
                loadingLayout.visibility = View.GONE
                animator1.pause(); animator2.pause(); animator3.pause(); animator4.pause()
            }
        }
    }

    private fun addPayment() {
        val mURL = URL("https://www.dongkye.tech/A5/addPayment.php")
        var variable = ""
        if (startTomorrow.isChecked) {
            variable = "checked"
        } else {
            variable = "unchecked"
        }
        var reqParam = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8")
        reqParam += "&" + URLEncoder.encode("recipient", "UTF-8") + "=" + URLEncoder.encode(displayName.text.toString().trim(), "UTF-8")
        reqParam += "&" + URLEncoder.encode("date", "UTF-8") + "=" + URLEncoder.encode(dateOnly.text.toString().trim(), "UTF-8")
        reqParam += "&" + URLEncoder.encode("startTomorrow", "UTF-8") + "=" + URLEncoder.encode(variable, "UTF-8")
        reqParam += "&" + URLEncoder.encode("amount", "UTF-8") + "=" + URLEncoder.encode(amount.text.toString(), "UTF-8")
        reqParam += "&" + URLEncoder.encode("token", "UTF-8") + "=" + URLEncoder.encode(baseCurrency, "UTF-8")
        reqParam += "&" + URLEncoder.encode("address", "UTF-8") + "=" + URLEncoder.encode(address, "UTF-8")
        reqParam += "&" + URLEncoder.encode("paymentName", "UTF-8") + "=" + URLEncoder.encode(paymentName.text.toString().trim(), "UTF-8")
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
                    result_msg.text = "네트워크 연결이 불안정합니다"
                    result_msg.visibility = View.VISIBLE
                } else if (jsonObj.getString("success") == "true") {
                    startActivity(Intent(applicationContext, subscribedPayments::class.java))
                } else if (jsonObj.getString("success") == "exists") {
                    result_msg.text = "이미 존재하는 결제명입니다"
                    result_msg.visibility = View.VISIBLE
                }
            }
        }
    }
    private fun getBaseCurrency() {
        val reqParam = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8")
        val mURL = URL("https://www.dongkye.tech/A5/getBaseCurrency.php")
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
                    startActivity(Intent(applicationContext, ConnectionError::class.java)
                        .putExtra("baseCurrency", baseCurrency)
                        .putExtra("startTomorrow", startTomorrow.isChecked)
                        .putExtra("date", dateOnly.text.toString().trim())
                        .putExtra("amount", amount.text.toString().trim())
                        .putExtra("recipient", displayName.text.toString().trim())
                        .putExtra("address", address)
                        .putExtra("receiver_id", receiver_id)
                        .putExtra("paymentName", paymentName.text.toString().trim())
                        .putExtra("user_nickname", user_nickname)
                    )
                } else if (jsonObj.getString("success") == "true") {
                    baseCurrency = jsonObj.getString("baseCurrency")
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