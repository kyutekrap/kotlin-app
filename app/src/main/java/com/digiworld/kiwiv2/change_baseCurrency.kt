package com.digiworld.kiwiv2

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
import java.util.*
import kotlin.collections.ArrayList

class change_baseCurrency : AppCompatActivity() {

    private lateinit var savebtn: TextView
    private lateinit var result_msg: TextView
    private lateinit var error_msg: TextView
    private var username: String = ""
    private lateinit var loadingLayout: LinearLayout
    private lateinit var loading1: View
    private lateinit var loading2: View
    private lateinit var loading3: View
    private lateinit var loading4: View
    private lateinit var menuBurger: ImageView
    private lateinit var recyclerView: RecyclerView
    private var currencyList: ArrayList<CurrencyModel> = ArrayList()
    private var original_baseCurrency: String = ""
    private var baseCurrency: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_base_currency)

        savebtn = findViewById(R.id.savebtn)
        result_msg = findViewById(R.id.result_msg)
        error_msg = findViewById(R.id.error_msg)
        loadingLayout = findViewById(R.id.loadinglayout)
        loading1 = findViewById(R.id.loading1)
        loading2 = findViewById(R.id.loading2)
        loading3 = findViewById(R.id.loading3)
        loading4 = findViewById(R.id.loading4)
        menuBurger = findViewById(R.id.menu_burger)

        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)

        menuBurger.setOnClickListener{
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
            if (extras.containsKey("baseCurrency")) {
                if (extras.getString("baseCurrency") == "") {
                    getBaseCurrency()
                } else {
                    original_baseCurrency = extras.getString("baseCurrency").toString()
                    baseCurrency = extras.getString("baseCurrency").toString()
                }
            }
        }

        try {
            loadingLayout.visibility = View.VISIBLE
            animator1.start(); animator2.start(); animator3.start(); animator4.start()
            loadCurrencies()
            val adapter = CurrencyAdapter(currencyList, this, username, baseCurrency, recyclerView)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
            loadingLayout.visibility = View.GONE
            animator1.pause(); animator2.pause(); animator3.pause(); animator4.pause()
        } catch (e: Exception) {
            // Log.d("Error: ", e.toString())
        }

        savebtn.setSafeOnClickListener {
            if (loadingLayout.visibility != View.VISIBLE) {
                loadingLayout.visibility = View.VISIBLE
                animator1.start(); animator2.start(); animator3.start(); animator4.start()
                if (original_baseCurrency != "") {
                    if (original_baseCurrency != baseCurrency) {
                        changeBaseCurrency(baseCurrency, animator1, animator2, animator3, animator4)
                    }
                } else {
                    changeBaseCurrency(baseCurrency, animator1, animator2, animator3, animator4)
                }
            }
        }
    }

    private fun changeBaseCurrency(baseCurrency: String, animator1: ObjectAnimator, animator2: ObjectAnimator, animator3: ObjectAnimator, animator4: ObjectAnimator) {
        val mURL = URL("https://www.dongkye.tech/A5/changeBaseCurrency.php?data=$baseCurrency")
        val reqParam = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(intent.extras?.getString("username").toString(), "UTF-8")
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
                    animator1.pause(); animator2.pause(); animator3.pause(); animator4.pause()
                    loadingLayout.visibility = View.GONE
                    result_msg.text = "네트워크 연결이 불안정합니다"
                    result_msg.visibility = View.VISIBLE
                } else if (jsonObj.getString("success") == "true") {
                    startActivity(Intent(applicationContext, MainParent::class.java)
                        .putExtra("context", "setting")
                    )
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
                    baseCurrency = "KRW"
                    error_msg.visibility = View.VISIBLE
                } else if (jsonObj.getString("success") == "true") {
                    baseCurrency = jsonObj.getString("baseCurrency")
                    original_baseCurrency = jsonObj.getString("baseCurrency")
                }
            }
        }
    }
    private fun loadCurrencies() {
        val mURL = URL("https://www.dongkye.tech/A5/currencies.php")
        val reqParam = URLEncoder.encode("baseCurrency", "UTF-8") + "=" + URLEncoder.encode(baseCurrency, "UTF-8")
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
                        val tempData = CurrencyModel(
                            baseInfo.getString("currencyName"),
                            baseInfo.getString("currencyValue")
                        )
                        currencyList.add(tempData)
                    }
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

    class CurrencyAdapter(private val mList: List<CurrencyModel>, private val context: Context, private val username: String, private var baseCurrency: String, private val recyclerView: RecyclerView) : RecyclerView.Adapter<CurrencyAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.choose_currency_recyclerview, parent, false)

            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val ItemsViewModel = mList[position]

            if (ItemsViewModel.currencyName == baseCurrency) {
                holder.linearLayout.background =
                    ContextCompat.getDrawable(context, R.drawable.login_form_edittext_focused)
            } else {
                holder.linearLayout.background =
                    ContextCompat.getDrawable(context, R.drawable.login_form_edittext)
            }

            holder.currencyName.text = ItemsViewModel.currencyName
            holder.currentRate.text = ItemsViewModel.currencyValue

            holder.itemView.setOnClickListener {
                baseCurrency = ItemsViewModel.currencyName
                val adapter = CurrencyAdapter(mList, context, username, ItemsViewModel.currencyName, recyclerView)
                recyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        }

        override fun getItemCount(): Int {
            return mList.size
        }

        class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
            val currencyName: TextView = itemView.findViewById(R.id.currencyName)
            val currentRate: TextView = itemView.findViewById(R.id.currentRate)
            val linearLayout: LinearLayout = itemView.findViewById(R.id.linearLayout)
        }
    }
}