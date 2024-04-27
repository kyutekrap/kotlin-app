package com.digiworld.kiwiv2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class pay_with_custom_keypad : AppCompatActivity() {

    private lateinit var nextButton: TextView
    private lateinit var recipient: TextView
    private lateinit var total: TextView
    private lateinit var one: TextView
    private lateinit var two: TextView
    private lateinit var three: TextView
    private lateinit var four: TextView
    private lateinit var five: TextView
    private lateinit var six: TextView
    private lateinit var seven: TextView
    private lateinit var eight: TextView
    private lateinit var nine: TextView
    private lateinit var zero: TextView
    private lateinit var dot: TextView
    private lateinit var del: TextView
    private lateinit var baseCurrency: TextView
    private lateinit var label: TextView
    private lateinit var error_msg: TextView
    private var recipient_address: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pay_with_custom_keypad)

        nextButton = findViewById(R.id.nextButton)
        recipient = findViewById(R.id.recipient)
        total = findViewById(R.id.total)
        one = findViewById(R.id.one)
        two = findViewById(R.id.two)
        three = findViewById(R.id.three)
        four = findViewById(R.id.four)
        five = findViewById(R.id.five)
        six = findViewById(R.id.six)
        seven = findViewById(R.id.seven)
        eight = findViewById(R.id.eight)
        nine = findViewById(R.id.nine)
        zero = findViewById(R.id.zero)
        dot = findViewById(R.id.dot)
        del = findViewById(R.id.del)
        label = findViewById(R.id.label)
        error_msg = findViewById(R.id.error_msg)
        baseCurrency = findViewById(R.id.baseCurrency)

        val sharedPreference =  getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val username = sharedPreference.getString("username", "").toString()

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey("recipient")) {
                recipient.text = extras.getString("recipient")
            } else {
                finish()
            }
            if (extras.containsKey("baseCurrency")) {
                baseCurrency.text = extras.getString("baseCurrency")
            } else {
                getBaseCurrency(username)
            }
            if (extras.containsKey("recipient_address")) {
                recipient_address = extras.getString("recipient_address").toString()
            } else {
                finish()
            }
        }

        nextButton.setSafeOnClickListener {
            var stop=0
            val thisTotal: String = total.text.toString()
            if (thisTotal.contains(".")) {
                if (baseCurrency.text.toString().trim() == "KRW") {
                    error_msg.text = "소수점 2자리까지 지원됩니다"
                    stop=1
                } else if (baseCurrency.text.toString().trim() == "BTC") {
                    error_msg.text = "소수점 6자리까지 지원됩니다"
                    stop=1
                }
            }
            if (stop==0) {
                if (thisTotal != "") {
                    if (thisTotal.toFloat() != 0F) {
                        startActivity(Intent(this, passcode_verification::class.java)
                            .putExtra("total", thisTotal)
                            .putExtra("recipient", recipient.text.toString().trim())
                            .putExtra("recipient_address", recipient_address)
                            .putExtra("username", username)
                            .putExtra("baseCurrency", baseCurrency.text.toString().trim())
                        )
                    }
                }
            }
        }

        one.setOnClickListener {
            updateValue(total, "1")
        }
        two.setOnClickListener {
            updateValue(total, "2")
        }
        three.setOnClickListener {
            updateValue(total, "3")
        }
        four.setOnClickListener {
            updateValue(total, "4")
        }
        five.setOnClickListener {
            updateValue(total, "5")
        }
        six.setOnClickListener {
            updateValue(total, "6")
        }
        seven.setOnClickListener {
            updateValue(total, "7")
        }
        eight.setOnClickListener {
            updateValue(total, "8")
        }
        nine.setOnClickListener {
            updateValue(total, "9")
        }
        zero.setOnClickListener {
            updateValue(total, "0")
        }
        dot.setOnClickListener {
            var thisTotal: String = total.text.toString()
            if (!thisTotal.contains(".", ignoreCase=true)) {
                if (thisTotal != "") {
                    if (thisTotal.toFloat() != 0F) {
                        thisTotal+="."
                    } else {
                        thisTotal="0."
                    }
                }
                total.text = thisTotal
            }
        }
        del.setOnClickListener {
            var thisTotal: String = total.text.toString()
            if (thisTotal != "" && thisTotal != "0.00") {
                thisTotal = thisTotal.dropLast(1)
                total.text = thisTotal
            }
        }
    }

    fun updateValue(total: TextView, value: String) {
        var thisTotal: String = total.text.toString()
        if (thisTotal == "0.00") {
            total.text = value
        } else if (thisTotal != "") {
            if (thisTotal.toFloat() == 0F) {
                if (thisTotal.contains(".")) {
                    total.text = total.text.toString()+value
                } else {
                    total.text = value
                }
            }
        }
        if (thisTotal == total.text.toString()) {
            thisTotal+=value
            total.text = thisTotal
        }
    }

    private fun getBaseCurrency(username: String) {
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
                    baseCurrency.text = "KRW"
                    label.text = "설정한 기초통화가 아니라면 앱을 재시작하세요"
                } else if (jsonObj.getString("success") == "true") {
                    baseCurrency.text = jsonObj.getString("baseCurrency")
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