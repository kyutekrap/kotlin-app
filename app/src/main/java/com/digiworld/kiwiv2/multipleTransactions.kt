package com.digiworld.kiwiv2

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.app.Dialog
import android.content.ClipData.Item
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.StrictMode
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.CycleInterpolator
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.zxing.integration.android.IntentIntegrator
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class multipleTransactions : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    var username: String = ""
    var recipientList: ArrayList<MultipleTransactionModel> = ArrayList()
    private lateinit var listView: RecyclerView
    var liveList: ArrayList<MultipleTransactionModel> = ArrayList()
    private lateinit var savebtn: TextView
    private lateinit var noResult: TextView
    private lateinit var qrCamera: TextView
    var _adapter = LiveListAdapter(liveList, this)
    var editedList: ArrayList<MultipleTransactionValueModel> = ArrayList()
    private var baseCurrency: String = ""
    private lateinit var loadingLayout: LinearLayout
    private lateinit var loading1: View
    private lateinit var loading2: View
    private lateinit var loading3: View
    private lateinit var loading4: View
    private lateinit var menuBurger: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiple_transactions)

        recyclerView = findViewById(R.id.recyclerView)
        listView = findViewById(R.id.listView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        listView.layoutManager = LinearLayoutManager(this)

        loadingLayout = findViewById(R.id.loadinglayout)
        loading1 = findViewById(R.id.loading1)
        loading2 = findViewById(R.id.loading2)
        loading3 = findViewById(R.id.loading3)
        loading4 = findViewById(R.id.loading4)
        savebtn = findViewById(R.id.savebtn)
        qrCamera = findViewById(R.id.qrCamera)
        menuBurger = findViewById(R.id.menu_burger)

        menuBurger.setOnClickListener {
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

        qrCamera.setOnClickListener {
            checkCameraPermissions(this)
            if (checkCameraHardware(this)) {
                initQRcodeScanner()
            } else {
                // no camera
            }
        }

        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey("baseCurrency")) {
                baseCurrency = extras.getString("baseCurrency").toString()
            }
        }

        savebtn.setOnClickListener {
            if (liveList.size > 0) {
                val dialog = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.activity_multiple_transactions_add_amount)
                dialog.show()

                val savebtn = dialog.findViewById(R.id.savebtn) as TextView
                val errorMsg = dialog.findViewById(R.id.error_msg) as TextView
                val cancel = dialog.findViewById(R.id.cancel) as ImageView
                val checked = dialog.findViewById(R.id.checked) as TextView
                val recyclerview = dialog.findViewById(R.id.recyclerView) as RecyclerView
                recyclerview.layoutManager = LinearLayoutManager(this)

                for (index in liveList) {
                    val tempData = MultipleTransactionValueModel(
                        index.displayName,
                        index.toAddress,
                        0F
                    )
                    editedList.add(tempData)
                }

                val listAdapter = ListAdapter(editedList, this, errorMsg, savebtn, checked, cancel)
                recyclerview.adapter = listAdapter
                listAdapter.notifyDataSetChanged()

                cancel.setOnClickListener{
                    editedList.clear()
                    dialog.dismiss()
                }

                savebtn.setSafeOnClickListener{
                    if (loadingLayout.visibility != View.VISIBLE) {
                        for (index in editedList) {
                            if (index.amount == 0F) {
                                errorMsg.text = "금액을 입력하세요"
                                savebtn.visibility = View.GONE
                            } else {
                                errorMsg.text = ""
                                savebtn.visibility = View.GONE
                                checked.visibility = View.VISIBLE
                            }
                        }
                    }
                }

                checked.setSafeOnClickListener{
                    loadingLayout.visibility = View.VISIBLE
                    animator1.start(); animator2.start(); animator3.start(); animator4.start()
                    sendData(errorMsg, animator1, animator2, animator3, animator4)
                }
            }
        }

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val sharedPreference =  getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        username = if (sharedPreference.contains("username")) {
            sharedPreference.getString("username", "").toString()
        } else {
            ""
        }

        try {
            loadingLayout.visibility = View.VISIBLE
            animator1.start(); animator2.start(); animator3.start(); animator4.start()
            listView.adapter = _adapter
            getTransactionHistory(username)
            val adapter = MultipleTransactionAdapter(recipientList, liveList, _adapter, this)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
            loadingLayout.visibility = View.GONE
            animator1.pause(); animator2.pause(); animator3.pause(); animator4.pause()
        } catch (e: Exception) {
            // Log.d("Error: ", e.toString())
        }
    }

    private fun sendData(errorMsg: TextView, animator1: ObjectAnimator, animator2: ObjectAnimator, animator3: ObjectAnimator, animator4: ObjectAnimator) {
        var dataString = ""
        for (index in editedList) {
            if (dataString == "") {
                dataString = index.amount.toString() + "-" + index.displayName + "-" + index.toAddress
            } else {
                dataString += "_" + index.amount.toString() + "-" + index.displayName + "-" + index.toAddress
            }
        }
        val mURL = URL("https://www.dongkye.tech/A5/addMultipleTransactions.php")
        var reqParam = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8")
        reqParam += "&" + URLEncoder.encode("baseCurrency", "UTF-8") + "=" + URLEncoder.encode(baseCurrency, "UTF-8")
        reqParam += "&" + URLEncoder.encode("dataString", "UTF-8") + "=" + URLEncoder.encode(dataString, "UTF-8")
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
                animator1.pause(); animator2.pause(); animator3.pause(); animator4.pause()
                if (jsonObj.getString("success") == "false") {
                    errorMsg.text = "네트워크 연결이 불안정합니다"
                } else if (jsonObj.getString("success") == "insufficient") {
                    errorMsg.text = "잔금이 부족합니다"
                } else {
                    startActivity(Intent(applicationContext, MainParent::class.java)
                        .putExtra("context", "main")
                        .putExtra("subcontext", "personal_transactions")
                    )
                }
            }
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
            if (intentResult.contents != null) {
                val _displayName = intentResult.contents.split("::")[0]
                val _receiverId = intentResult.contents.split("::")[1]
                val tempData = MultipleTransactionModel(
                    _displayName,
                    _receiverId
                )
                liveList.add(tempData)
                _adapter.notifyDataSetChanged()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun getTransactionHistory(username: String) {
        val mURL = URL("https://www.dongkye.tech/A5/getRecipients.php")
        val reqParam = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8")
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
                        val tempData = MultipleTransactionModel(
                            baseInfo.getString("displayName"),
                            baseInfo.getString("toAddress")
                        )
                        recipientList.add(tempData)
                    }
                } else {
                    recyclerView.visibility = View.GONE
                    noResult.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun checkCameraHardware(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }
    fun checkCameraPermissions(context: Context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                (context as Activity?)!!, arrayOf(Manifest.permission.CAMERA),
                100
            )
        }
    }

    inner class LiveListAdapter(
        private val mList: List<MultipleTransactionModel>,
        private val context: Context
    ) : RecyclerView.Adapter<LiveListAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.multiple_transactions_listview, parent, false)
            return ViewHolder(view)
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val ItemsViewModel = mList[position]
            holder.displayName.text = ItemsViewModel.displayName
            holder.toAddress.text = ItemsViewModel.toAddress
            holder.itemView.setOnClickListener {
                val adapter = MultipleTransactionAdapter(recipientList, liveList, _adapter, context)
                recyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
                val actualPosition = holder.adapterPosition
                liveList.removeAt(actualPosition)
                _adapter.notifyDataSetChanged()
            }
        }
        override fun getItemCount(): Int {
            return mList.size
        }
        inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
            val displayName: TextView = itemView.findViewById(R.id.displayName)
            val toAddress: TextView = itemView.findViewById(R.id.toAddress)
        }
    }
}

class ListAdapter(
    private val mList: List<MultipleTransactionValueModel>,
    private val context: Context,
    private val errorMsg: TextView,
    private val savebtn: TextView,
    private val checked: TextView,
    private val cancel: ImageView
) : RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.multiple_transactions_add_amount_recyclerview, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]
        holder.displayName.text = ItemsViewModel.displayName
        holder.amount.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                holder.amount.background = ContextCompat.getDrawable(
                    context,
                    R.drawable.login_form_edittext_focused
                )
            } else {
                holder.amount.background =
                    ContextCompat.getDrawable(context, R.drawable.login_form_edittext)
            }
        }
        holder.amount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                ItemsViewModel.amount = s.toString().toFloat()
                if (errorMsg.text.toString() != "") {
                    errorMsg.text = ""
                }
                if (savebtn.visibility == View.GONE) {
                    savebtn.visibility = View.VISIBLE
                }
                if (checked.visibility == View.VISIBLE) {
                    checked.visibility = View.GONE
                }
            }
        })
    }
    override fun getItemCount(): Int {
        return mList.size
    }
    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val displayName: TextView = itemView.findViewById(R.id.displayName)
        val amount: EditText = itemView.findViewById(R.id.amount)
    }
}

class MultipleTransactionAdapter(
    private val mList: List<MultipleTransactionModel>,
    private val liveList: ArrayList<MultipleTransactionModel>,
    private val _adapter: multipleTransactions.LiveListAdapter,
    private val context: Context
) : RecyclerView.Adapter<MultipleTransactionAdapter.ViewHolder>() {
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val displayName: TextView = itemView.findViewById(R.id.displayName)
        val toAddress: TextView = itemView.findViewById(R.id.toAddress)
        val selected: TextView = itemView.findViewById(R.id.selected)
        val linearLayout: LinearLayout = itemView.findViewById(R.id.linearLayout)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.multiple_transactions_recyclerview, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]
        holder.displayName.text = ItemsViewModel.displayName
        holder.toAddress.text = ItemsViewModel.toAddress
        var i=0
        while (i < liveList.size) {
            if (liveList[i].toAddress == ItemsViewModel.toAddress) {
                holder.linearLayout.background = ContextCompat.getDrawable(
                    context,
                    R.drawable.login_form_edittext_focused
                )
            }
            i++
        }
        holder.itemView.setOnClickListener {
            i=0
            var stop=0
            while (i < liveList.size) {
                if (liveList[i].toAddress == ItemsViewModel.toAddress) {
                    holder.linearLayout.background = ContextCompat.getDrawable(
                        context,
                        R.drawable.login_form_edittext
                    )
                    liveList.removeAt(i)
                    _adapter.notifyDataSetChanged()
                    stop=1
                }
                i++
            }
            if (stop==0) {
                holder.linearLayout.background = ContextCompat.getDrawable(
                    context,
                    R.drawable.login_form_edittext_focused
                )
                val _displayName = holder.displayName.text.toString()
                val _toAddress = holder.toAddress.text.toString()
                val tempData = MultipleTransactionModel(
                    _displayName,
                    _toAddress
                )
                liveList.add(tempData)
                _adapter.notifyDataSetChanged()
            }
        }
    }
    override fun getItemCount(): Int {
        return mList.size
    }
}

fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}