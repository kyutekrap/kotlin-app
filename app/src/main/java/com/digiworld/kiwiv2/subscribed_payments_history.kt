package com.digiworld.kiwiv2

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.view.ContextThemeWrapper
import android.view.View
import android.view.animation.Animation
import android.view.animation.CycleInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
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

class subscribed_payments_history : AppCompatActivity() {

    private lateinit var paymentName: TextView
    private lateinit var amount: TextView
    private lateinit var date: TextView
    private lateinit var currency: TextView
    private lateinit var recipient: TextView
    private lateinit var recyclerView: RecyclerView
    private var subscribedPaymentsHistoryList: ArrayList<ManageSubscribedPaymentsHistoryModel> = ArrayList()
    private lateinit var noRecord: TextView
    private var paymentId: String = ""
    private lateinit var loadingLayout: LinearLayout
    private lateinit var loading1: View
    private lateinit var loading2: View
    private lateinit var loading3: View
    private lateinit var loading4: View
    private lateinit var cross: ImageView
    private lateinit var address: TextView
    private lateinit var cancel_subscription: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscribed_payments_history)

        paymentName = findViewById(R.id.paymentName)
        amount = findViewById(R.id.amount)
        date = findViewById(R.id.date)
        currency = findViewById(R.id.currency)
        recipient = findViewById(R.id.recipient)
        loadingLayout = findViewById(R.id.loadinglayout)
        loading1 = findViewById(R.id.loading1)
        loading2 = findViewById(R.id.loading2)
        loading3 = findViewById(R.id.loading3)
        loading4 = findViewById(R.id.loading4)
        noRecord = findViewById(R.id.noRecord)
        cross = findViewById(R.id.cross)
        address = findViewById(R.id.toAddress)
        cancel_subscription = findViewById(R.id.cancel_subscription)

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
            if (extras.containsKey("paymentName")) {
                paymentName.text = extras.getString("paymentName")
            }
            if (extras.containsKey("amount")) {
                amount.text = extras.getString("amount")
            }
            if (extras.containsKey("date")) {
                date.text = extras.getString("date")
            }
            if (extras.containsKey("currency")) {
                currency.text = extras.getString("currency")
            }
            if (extras.containsKey("recipient")) {
                recipient.text = extras.getString("recipient")
            }
            if (extras.containsKey("address")) {
                address.text = extras.getString("address")
            }
            if (extras.containsKey("paymentId")) {
                paymentId = extras.getString("paymentId").toString()
            }
            if (extras.containsKey("payment_date")) {
                date.text = extras.getString("payment_date")
            }
        }

        try {
            loadingLayout.visibility = View.VISIBLE
            animator1.start(); animator2.start(); animator3.start(); animator4.start()
            loadHistory()
            val adapter = manage_subscribed_payments_history_Adapter(subscribedPaymentsHistoryList)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
            loadingLayout.visibility = View.GONE
            animator1.pause(); animator2.pause(); animator3.pause(); animator4.pause()
        } catch (e: Exception) {
            // Log.d("Error: ", e.toString())
        }

        cancel_subscription.setSafeOnClickListener {
            val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.MyAlertDialogTheme))
            builder.setTitle("정기이체를 취소할까요?")
            builder.setPositiveButton("네") { _, _ ->
                loadingLayout.visibility = View.VISIBLE
                animator1.start(); animator2.start(); animator3.start(); animator4.start()
                cancelSubscription(animator1, animator2, animator3, animator4)
            }
            builder.setNegativeButton("아니오") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.setOnShowListener {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                    .setTextColor(Color.BLACK)
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                    .setTextColor(Color.BLACK)
            }
            dialog.show()
        }
    }

    private fun cancelSubscription(animator1: ObjectAnimator, animator2: ObjectAnimator, animator3: ObjectAnimator, animator4: ObjectAnimator) {
        val reqParam = URLEncoder.encode("paymentId", "UTF-8") + "=" + URLEncoder.encode(paymentId, "UTF-8")
        val mURL = URL("https://www.dongkye.tech/A5/cancelSubscription.php")
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
                loadingLayout.visibility = View.GONE
                animator1.pause(); animator2.pause(); animator3.pause(); animator4.pause()
                val jsonObj = JSONObject(response.toString())
                if (jsonObj.getString("success") == "false") {
                    val builder = AlertDialog.Builder(ContextThemeWrapper(applicationContext, R.style.MyAlertDialogTheme))
                    builder.setTitle("네트워크 연결이 불안정합니다")
                    val dialog = builder.create()
                    dialog.show()
                } else if (jsonObj.getString("success") == "true") {
                    startActivity(Intent(applicationContext, subscribedPayments::class.java))
                }
            }
        }
    }

    private fun loadHistory() {
        val reqParam = URLEncoder.encode("paymentId", "UTF-8") + "=" + URLEncoder.encode(paymentId, "UTF-8")
        val mURL = URL("https://www.dongkye.tech/A5/subscribed_payments_history.php")
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
                        val tempData = ManageSubscribedPaymentsHistoryModel(
                            baseInfo.getString("date"),
                            baseInfo.getString("result")
                        )
                        subscribedPaymentsHistoryList.add(tempData)
                    }
                } else {
                    noRecord.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
            }
        }
    }
}