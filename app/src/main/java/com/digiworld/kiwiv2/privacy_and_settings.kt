package com.digiworld.kiwiv2

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.math.BigInteger
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.security.MessageDigest

class privacy_and_settings : Fragment() {

    private lateinit var displayName: LinearLayout
    private lateinit var baseCurrency: LinearLayout
    private lateinit var manyTransactions: LinearLayout
    private lateinit var _subscribedPayments: LinearLayout
    private lateinit var _manage_subscribedPayments: LinearLayout
    private lateinit var _change_password: LinearLayout
    private lateinit var logout: LinearLayout
    private lateinit var displayName_txt: TextView
    private lateinit var baseCurrency_txt: TextView
    private var new_currency: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.privacy_and_settings, container, false)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val dialog = Dialog(requireContext(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
        dialog.setCancelable(true)

        displayName = view.findViewById(R.id.displayName)
        baseCurrency = view.findViewById(R.id.baseCurrency)
        manyTransactions = view.findViewById(R.id.manyTransactions)
        _subscribedPayments = view.findViewById(R.id.subscribedPayments)
        _manage_subscribedPayments = view.findViewById(R.id.manage_subscribedPayments)
        _change_password = view.findViewById(R.id.change_password)
        logout = view.findViewById(R.id.logout)
        displayName_txt = view.findViewById(R.id.displayName_txt)
        baseCurrency_txt = view.findViewById(R.id.baseCurrency_txt)

        displayName_txt.text = (activity as MainParent).displayName
        baseCurrency_txt.text = (activity as MainParent).baseCurrency

        displayName.setOnClickListener {
            dialog.setContentView(R.layout.activity_edit_display_name)
            dialog.show()

            val displayName = dialog.findViewById(R.id.displayName) as EditText
            val password = dialog.findViewById(R.id.password) as EditText
            val result_msg = dialog.findViewById(R.id.result_msg) as TextView
            val savebtn = dialog.findViewById(R.id.savebtn) as TextView
            val menuBurger = dialog.findViewById(R.id.menu_burger) as ImageView

            displayName.setText((activity as MainParent).displayName)

            displayName.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    displayName.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.login_form_edittext_focused
                    )
                } else {
                    displayName.background =
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.login_form_edittext
                        )
                }
            }
            password.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    password.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.login_form_edittext_focused
                    )
                } else {
                    password.background =
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.login_form_edittext
                        )
                }
            }

            menuBurger.setOnClickListener {
                dialog.dismiss()
            }
            savebtn.setSafeOnClickListener {
                if (displayName.text.toString().trim() != "" && password.text.toString().trim() != "") {
                    if (displayName.text.toString().trim() != (activity as MainParent).displayName) {
                        (activity as MainParent).loadingLayout.visibility = View.VISIBLE
                        (activity as MainParent).animator1.start(); (activity as MainParent).animator2.start()
                        (activity as MainParent).animator3.start(); (activity as MainParent).animator4.start()
                        sendPostRequest(
                            displayName.text.toString(),
                            password.text.toString(),
                            dialog,
                            result_msg
                        )
                    }
                }
            }
        }
        baseCurrency.setOnClickListener {
            dialog.setContentView(R.layout.activity_change_base_currency)
            dialog.show()

            (activity as MainParent).loadingLayout.visibility = View.VISIBLE
            (activity as MainParent).animator1.start(); (activity as MainParent).animator2.start()
            (activity as MainParent).animator3.start(); (activity as MainParent).animator4.start()

            val savebtn = dialog.findViewById(R.id.savebtn) as TextView
            val result_msg = dialog.findViewById(R.id.result_msg) as TextView
            val menuBurger = dialog.findViewById(R.id.menu_burger) as ImageView
            val recyclerView = dialog.findViewById(R.id.recyclerview) as RecyclerView
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            val currencyList: ArrayList<CurrencyModel> = ArrayList()
            loadCurrencies(currencyList)
            new_currency = (activity as MainParent).baseCurrency
            val adapter = CurrencyAdapter(
                currencyList,
                requireContext(),
                recyclerView
            )
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
            (activity as MainParent).loadingLayout.visibility = View.GONE
            (activity as MainParent).animator1.pause(); (activity as MainParent).animator2.pause()
            (activity as MainParent).animator3.pause(); (activity as MainParent).animator4.pause()

            menuBurger.setOnClickListener {
                dialog.dismiss()
            }

            savebtn.setSafeOnClickListener {
                if ((activity as MainParent).loadingLayout.visibility != View.VISIBLE) {
                    if (new_currency != (activity as MainParent).baseCurrency) {
                        (activity as MainParent).loadingLayout.visibility = View.VISIBLE
                        (activity as MainParent).animator1.start(); (activity as MainParent).animator2.start()
                        (activity as MainParent).animator3.start(); (activity as MainParent).animator4.start()
                        changeBaseCurrency(new_currency, result_msg)
                    }
                }
            }
        }
        manyTransactions.setOnClickListener {
            startActivity(Intent(requireContext(), multipleTransactions::class.java)
                .putExtra("baseCurrency", (activity as MainParent).baseCurrency))
        }
        _subscribedPayments.setOnClickListener {
            startActivity(Intent(requireContext(), subscribedPayments::class.java))
        }
        _manage_subscribedPayments.setOnClickListener {
            dialog.setContentView(R.layout.activity_manage_subscribed_payments)
            dialog.show()

            val recyclerView = dialog.findViewById(R.id.recyclerView) as RecyclerView
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            val noRecord = dialog.findViewById(R.id.noRecord) as TextView
            val menuBurger = dialog.findViewById(R.id.menu_burger) as ImageView
            val subscribedPaymentsList: ArrayList<ManageSubscribedPaymentsModel> = ArrayList()

            menuBurger.setOnClickListener {
                dialog.dismiss()
            }

            (activity as MainParent).loadingLayout.visibility = View.VISIBLE
            (activity as MainParent).animator1.start(); (activity as MainParent).animator2.start()
            (activity as MainParent).animator3.start(); (activity as MainParent).animator4.start()
            loadPayments(subscribedPaymentsList, noRecord, recyclerView)
            val adapter = manage_subscribedPayments_Adapter(subscribedPaymentsList, requireContext(), "manage")
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
            (activity as MainParent).loadingLayout.visibility = View.GONE
            (activity as MainParent).animator1.pause(); (activity as MainParent).animator2.pause()
            (activity as MainParent).animator3.pause(); (activity as MainParent).animator4.pause()
        }
        _change_password.setOnClickListener {
            dialog.setContentView(R.layout.activity_change_password)
            dialog.show()

            val menuBurger = dialog.findViewById(R.id.menu_burger) as ImageView
            val original_password = dialog.findViewById(R.id.original_password) as EditText
            val new_password = dialog.findViewById(R.id.new_password) as EditText
            val check_password = dialog.findViewById(R.id.check_password) as EditText
            val save_btn = dialog.findViewById(R.id.savebtn) as TextView
            val result_msg = dialog.findViewById(R.id.result_msg) as TextView

            menuBurger.setOnClickListener {
                dialog.dismiss()
            }

            original_password.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    original_password.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.login_form_edittext_focused
                    )
                } else {
                    original_password.background =
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.login_form_edittext
                        )
                }
            }
            new_password.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    new_password.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.login_form_edittext_focused
                    )
                } else {
                    new_password.background =
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.login_form_edittext
                        )
                }
            }
            check_password.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    check_password.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.login_form_edittext_focused
                    )
                } else {
                    check_password.background =
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.login_form_edittext
                        )
                }
            }

            save_btn.setSafeOnClickListener {
                if ((activity as MainParent).loadingLayout.visibility != View.VISIBLE) {
                    if (original_password.text.toString().trim() != "" && new_password.text.toString().trim() != "") {
                        if (original_password.text.toString().trim() == new_password.text.toString().trim()) {
                            new_password.error = "비밀번호가 같습니다"
                        } else {
                            if (new_password.text.toString().trim() == check_password.text.toString().trim()) {
                                (activity as MainParent).loadingLayout.visibility = View.VISIBLE
                                (activity as MainParent).animator1.start(); (activity as MainParent).animator2.start()
                                (activity as MainParent).animator3.start(); (activity as MainParent).animator4.start()
                                changePassword(original_password.text.toString().trim(), new_password.text.toString().trim(), result_msg)
                            } else {
                                check_password.error = "비밀번호가 일치하지 않습니다"
                            }
                        }
                    }
                }
            }
        }

        logout.setOnClickListener {
            val dbHelper = FeedReaderDbHelper(requireContext())
            val db = dbHelper.writableDatabase
            db.execSQL("DELETE FROM ${FeedReaderDbHelper.FeedReaderContract.FeedEntry.TABLE_NAME}");
            val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()
            startActivity(Intent(requireContext(), login::class.java))
        }

        return view
    }

    private fun loadPayments(subscribedPaymentsList: ArrayList<ManageSubscribedPaymentsModel>, noRecord:TextView, recyclerView: RecyclerView) {
        val reqParam = URLEncoder.encode("receiver_id", "UTF-8") + "=" + URLEncoder.encode((activity as MainParent).receiver_id, "UTF-8")
        val mURL = URL("https://www.dongkye.tech/A5/manage_subscribed_payments.php")
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
                        val tempData = ManageSubscribedPaymentsModel(
                            baseInfo.getString("paymentName"),
                            baseInfo.getString("amount"),
                            baseInfo.getString("payment_date"),
                            baseInfo.getString("token"),
                            baseInfo.getString("recipient"),
                            baseInfo.getString("recipient_address"),
                            baseInfo.getString("paymentId")
                        )
                        subscribedPaymentsList.add(tempData)
                    }
                } else {
                    noRecord.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
            }
        }
    }

    fun changePassword(originalPassword:String, newPassword:String, result_msg:TextView) {
        var reqParam = URLEncoder.encode("originalPassword", "UTF-8") + "=" + URLEncoder.encode(originalPassword.sha256(), "UTF-8")
        reqParam += "&" + URLEncoder.encode("newPassword", "UTF-8") + "=" + URLEncoder.encode(newPassword.sha256(), "UTF-8")
        reqParam += "&" + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode((activity as MainParent).username, "UTF-8")
        val mURL = URL("https://www.dongkye.tech/A5/change_password.php")
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
                (activity as MainParent).loadingLayout.visibility = View.GONE
                (activity as MainParent).animator1.pause(); (activity as MainParent).animator2.pause()
                (activity as MainParent).animator3.pause(); (activity as MainParent).animator4.pause()
                if (jsonObj.getString("success") == "wrong") {
                    result_msg.text = "현재 비밀번호가 틀렸습니다"
                    result_msg.setTextColor(Color.parseColor("#FF0000"))
                    result_msg.visibility = View.VISIBLE
                } else if (jsonObj.getString("success") == "false") {
                    result_msg.text = "네트워크 연결이 불안정합니다"
                    result_msg.setTextColor(Color.parseColor("#FF0000"))
                    result_msg.visibility = View.VISIBLE
                } else if (jsonObj.getString("success") == "true") {
                    result_msg.text = "성공적으로 변경되었습니다"
                    result_msg.setTextColor(Color.parseColor("#2ca02c"))
                    result_msg.visibility = View.VISIBLE
                }
            }

        }
    }

    private fun changeBaseCurrency(baseCurrency: String, result_msg: TextView) {
        val mURL = URL("https://www.dongkye.tech/A5/changeBaseCurrency.php?data=$baseCurrency")
        val reqParam = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode((activity as MainParent).username, "UTF-8")
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
                    (activity as MainParent).animator1.pause(); (activity as MainParent).animator2.pause()
                    (activity as MainParent).animator3.pause(); (activity as MainParent).animator4.pause()
                    (activity as MainParent).loadingLayout.visibility = View.GONE
                    result_msg.text = "네트워크 연결이 불안정합니다"
                    result_msg.visibility = View.VISIBLE
                } else if (jsonObj.getString("success") == "true") {
                    startActivity(Intent(requireContext(), MainParent::class.java)
                        .putExtra("context", "setting")
                    )
                }
            }
        }
    }

    private fun loadCurrencies(currencyList: ArrayList<CurrencyModel>) {
        val mURL = URL("https://www.dongkye.tech/A5/currencies.php")
        val reqParam = URLEncoder.encode("baseCurrency", "UTF-8") + "=" + URLEncoder.encode((activity as MainParent).baseCurrency, "UTF-8")
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

    fun sendPostRequest(name:String, password:String, dialog:Dialog, result_msg:TextView) {
        var reqParam = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode((activity as MainParent).username, "UTF-8")
        reqParam += "&" + URLEncoder.encode("displayName", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8")
        reqParam += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password.sha256(), "UTF-8")
        val mURL = URL("https://www.dongkye.tech/A5/edit_displayName.php")
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
                    dialog.dismiss()
                    (activity as MainParent).displayName = name
                    displayName_txt.text = name
                } else {
                    (activity as MainParent).loadingLayout.visibility = View.GONE
                    if (jsonObj.getString("success") == "false") {
                        result_msg.text = "네트워크 연결이 불안정합니다"
                        result_msg.visibility = View.VISIBLE
                    } else if (jsonObj.getString("success") == "wrong") {
                        result_msg.text = "비밀번호가 틀렸습니다"
                        result_msg.visibility = View.VISIBLE
                    }
                }
                (activity as MainParent).animator1.pause(); (activity as MainParent).animator2.pause()
                (activity as MainParent).animator3.pause(); (activity as MainParent).animator4.pause()
                (activity as MainParent).loadingLayout.visibility = View.GONE
            }
        }
    }

    inner class CurrencyAdapter(val mList: List<CurrencyModel>, val context: Context, val recyclerView: RecyclerView) : RecyclerView.Adapter<CurrencyAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.choose_currency_recyclerview, parent, false)
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            return ViewHolder(view)
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val ItemsViewModel = mList[position]
            if (ItemsViewModel.currencyName == new_currency) {
                holder.linearLayout.background =
                    ContextCompat.getDrawable(context, R.drawable.login_form_edittext_focused)
            } else {
                holder.linearLayout.background =
                    ContextCompat.getDrawable(context, R.drawable.login_form_edittext)
            }
            holder.currencyName.text = ItemsViewModel.currencyName
            holder.currentRate.text = ItemsViewModel.currencyValue
            holder.itemView.setOnClickListener {
                new_currency = ItemsViewModel.currencyName
                val adapter = CurrencyAdapter(mList, context, recyclerView)
                recyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        }
        override fun getItemCount(): Int {
            return mList.size
        }
        inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
            val currencyName: TextView = itemView.findViewById(R.id.currencyName)
            val currentRate: TextView = itemView.findViewById(R.id.currentRate)
            val linearLayout: LinearLayout = itemView.findViewById(R.id.linearLayout)
        }
    }

    private fun String.sha256(): String {
        val md = MessageDigest.getInstance("SHA-256")
        return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
    }
}