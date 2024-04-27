package com.digiworld.kiwiv2

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.CycleInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.zxing.integration.android.IntentIntegrator
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class subscribed_payments_find : AppCompatActivity() {

    private lateinit var noResult: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var userCamera: TextView
    private var transactionList: ArrayList<RecipientFindModel> = ArrayList()
    private var username: String = ""
    private var displayName: String = ""
    private var dateOnly: String = ""
    private var startTomorrow: Boolean = false
    private var amount: String = ""
    private var baseCurrency: String = ""
    private var receiver_id: String = ""
    private var paymentName: String = ""
    private var user_nickname: String = ""
    private lateinit var loadingLayout: LinearLayout
    private lateinit var loading1: View
    private lateinit var loading2: View
    private lateinit var loading3: View
    private lateinit var loading4: View
    private lateinit var cross: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscribed_payments_find)

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

        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey("paymentName")) {
                paymentName = extras.getString("paymentName").toString()
            }
            if (extras.containsKey("date")) {
                dateOnly = extras.getString("date").toString()
            }
            if (extras.containsKey("startTomorrow")) {
                startTomorrow = extras.getBoolean("startTomorrow")
            }
            if (extras.containsKey("amount")) {
                amount = extras.getString("amount").toString()
            }
            if (extras.containsKey("baseCurrency")) {
                baseCurrency = extras.getString("baseCurrency").toString()
            }
            if (extras.containsKey("username")) {
                username = extras.getString("username").toString()
            }
            if (extras.containsKey("receiver_id")) {
                receiver_id = extras.getString("receiver_id").toString()
            }
            if (extras.containsKey("user_nickname")) {
                user_nickname = extras.getString("user_nickname").toString()
            }
        }

        noResult = findViewById(R.id.noResult)
        recyclerView = findViewById(R.id.recyclerView)
        userCamera = findViewById(R.id.useCamera)

        userCamera.setOnClickListener {
            checkCameraPermissions(this)
            if (checkCameraHardware(this)) {
                initQRcodeScanner()
            } else {
                Toast.makeText(this, "카메라 권한이 없습니다", Toast.LENGTH_LONG).show()
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(this)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        try {
            loadingLayout.visibility = View.VISIBLE
            animator1.start(); animator2.start(); animator3.start(); animator4.start()
            loadHistory()
            val adapter = RecipientFindAdapter(transactionList, this)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
            loadingLayout.visibility = View.GONE
            animator1.pause(); animator2.pause(); animator3.pause(); animator4.pause()
        } catch (e: Exception) {
            // Log.d("Error: ", e.toString())
        }
    }

    private fun initQRcodeScanner() {
        val intentIntegrator = IntentIntegrator(this)
        intentIntegrator.setBeepEnabled(false)
        intentIntegrator.captureActivity = QReaderActivity2::class.java
        intentIntegrator.initiateScan()
    }
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        val intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (intentResult != null) {
            if (intentResult.contents == null) {
                Toast.makeText(this, "취소되었습니다", Toast.LENGTH_LONG).show()
            } else {
                if (intentResult.contents.contains("::")) {
                    val displayName = intentResult.contents.split("::")[0]
                    val address = intentResult.contents.split("::")[1]
                    val intent = Intent(this, subscribed_payments_add::class.java)
                    intent.putExtra("recipient", displayName)
                    intent.putExtra("address", address)
                    intent.putExtra("username", username)
                    intent.putExtra("baseCurrency", baseCurrency)
                    intent.putExtra("startTomorrow", startTomorrow)
                    intent.putExtra("date", dateOnly)
                    intent.putExtra("amount", amount)
                    intent.putExtra("paymentName", paymentName)
                    intent.putExtra("user_nickname", user_nickname)
                    startActivity(intent)
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun checkCameraHardware(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }
    private fun checkCameraPermissions(context: Context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                (context as Activity?)!!, arrayOf(Manifest.permission.CAMERA),
                100
            )
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
                    noResult.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
            }
        }
    }

    inner class RecipientFindAdapter(private val mList: List<RecipientFindModel>, val context: Context) : RecyclerView.Adapter<RecipientFindAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.subscribed_payments_find_recyclerview, parent, false)

            return ViewHolder(view)
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val ItemsViewModel = mList[position]

            holder.datetime.text = ItemsViewModel.datetime
            holder.address.text = ItemsViewModel.recipient + " : " + ItemsViewModel.toAddress
            if (ItemsViewModel.action == "1") {
                holder.action.text = "입금"
                holder.amount.text = ItemsViewModel.actual_amount
                holder.amount.setTextColor(Color.parseColor("#1fc71f"))
            } else {
                holder.action.text = "출금"
                holder.amount.text = "-" + ItemsViewModel.actual_amount
                holder.amount.setTextColor(Color.parseColor("#00BFFF"))
            }
            holder.token.text = ItemsViewModel.token

            holder.itemView.setOnClickListener {
                val intent = Intent(context, subscribed_payments_add::class.java)
                intent.putExtra("recipient", ItemsViewModel.recipient)
                intent.putExtra("address", ItemsViewModel.toAddress)
                intent.putExtra("baseCurrency", baseCurrency)
                intent.putExtra("receiver_id", receiver_id)
                intent.putExtra("date", dateOnly)
                intent.putExtra("startTomorrow", startTomorrow)
                intent.putExtra("amount", amount)
                intent.putExtra("paymentName", paymentName)
                intent.putExtra("user_nickname", user_nickname)
                context.startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return mList.size
        }

        inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
            val datetime: TextView = itemView.findViewById(R.id.datetime)
            val amount: TextView = itemView.findViewById(R.id.amount)
            val address: TextView = itemView.findViewById(R.id.address)
            val action: TextView = itemView.findViewById(R.id.action)
            val token: TextView = itemView.findViewById(R.id.token)
        }
    }
}