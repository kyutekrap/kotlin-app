package com.digiworld.kiwiv2

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.MPPointF
import com.klipwallet.app2app.api.Klip
import com.klipwallet.app2app.api.KlipCallback
import com.klipwallet.app2app.api.request.AuthRequest
import com.klipwallet.app2app.api.request.TokenTxRequest
import com.klipwallet.app2app.api.request.model.BAppInfo
import com.klipwallet.app2app.api.response.KlipErrorResponse
import com.klipwallet.app2app.api.response.KlipResponse
import com.klipwallet.app2app.exception.KlipRequestException
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class main : Fragment() {

    private lateinit var withdraw: TextView
    private lateinit var deposit: TextView
    private lateinit var baseCurrency: TextView
    private lateinit var totalAmount: TextView
    private lateinit var cardType: TextView
    private lateinit var totalDistribution: TextView
    private lateinit var recyclerView: RecyclerView
    private var distributionList: ArrayList<distributionModel> = ArrayList()
    private lateinit var _announcement: LinearLayout
    private lateinit var announcement_title: TextView
    private lateinit var pieChart: PieChart
    private lateinit var personalTransactionHistory: TextView
    private lateinit var totalActivity: TextView
    lateinit var request_key : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val dialog = Dialog(requireContext(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
        dialog.setCancelable(true)

        withdraw = view.findViewById(R.id.withdraw)
        deposit = view.findViewById(R.id.deposit)
        baseCurrency = view.findViewById(R.id.baseCurrency)
        totalAmount = view.findViewById(R.id.totalAmount)
        cardType = view.findViewById(R.id.cardType)
        totalDistribution = view.findViewById(R.id.totalDistribution)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        _announcement = view.findViewById(R.id.announcement)
        announcement_title = view.findViewById(R.id.announcement_title)
        pieChart = view.findViewById(R.id.pieChart)
        personalTransactionHistory = view.findViewById(R.id.personalTransactionsHistory)
        totalActivity = view.findViewById(R.id.totalActivity)

        if ((activity as MainParent).subcontext == "personal_transactions") {
            dialog.setContentView(R.layout.personal_transaction_history)
            dialog.show()

            val recyclerView = dialog.findViewById(R.id.recyclerView) as RecyclerView
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            val noRecord = dialog.findViewById(R.id.noRecord) as TextView
            val cross = dialog.findViewById(R.id.cross) as ImageView
            val transactionList: ArrayList<RecipientFindModel> = ArrayList()

            cross.setOnClickListener {
                dialog.dismiss()
            }

            (activity as MainParent).loadingLayout.visibility = View.VISIBLE
            (activity as MainParent).animator1.start(); (activity as MainParent).animator2.start()
            (activity as MainParent).animator3.start(); (activity as MainParent).animator4.start()
            loadHistory(transactionList, noRecord)
            val adapter = RecipientFindAdapter(transactionList)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
            (activity as MainParent).loadingLayout.visibility = View.GONE
            (activity as MainParent).animator1.pause(); (activity as MainParent).animator2.pause()
            (activity as MainParent).animator3.pause(); (activity as MainParent).animator4.pause()
            (activity as MainParent).subcontext = ""
        }

        if ((activity as MainParent).subcontext != "" && (activity as MainParent).subcontext != "personal_transactions") {
            dialog.setContentView(R.layout.personal_product_view)
            dialog.show()

            val fundName = dialog.findViewById(R.id.fundName) as TextView
            val seeFakeData = dialog.findViewById(R.id.seeFakeData) as TextView
            val total = dialog.findViewById(R.id.total) as TextView
            val average = dialog.findViewById(R.id.average) as TextView
            val recyclerView = dialog.findViewById(R.id.recyclerView) as RecyclerView
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            val increase = dialog.findViewById(R.id.increase) as TextView
            val decrease = dialog.findViewById(R.id.decrease) as TextView
            val noRecord = dialog.findViewById(R.id.noRecord) as TextView
            val bottomNav = dialog.findViewById(R.id.bottomNav) as LinearLayout
            val productViewList: ArrayList<ProductViewModel> = ArrayList()

            (activity as MainParent).loadingLayout.visibility = View.VISIBLE
            (activity as MainParent).animator1.start(); (activity as MainParent).animator2.start()
            (activity as MainParent).animator3.start(); (activity as MainParent).animator4.start()

            fundName.text = (activity as MainParent).subcontext

            if (fundName.text.toString().trim() == "포트폴리오") {
                bottomNav.visibility = View.GONE
                seeFakeData.visibility = View.GONE
                getProductInfo_portfolio(total, average)
                loadProductView_portfolio(productViewList, noRecord, recyclerView)
            } else {
                loadProductView((activity as MainParent).subcontext, productViewList, total, average, noRecord, recyclerView)
            }
            val adapter = ProductViewAdapter(productViewList)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
            (activity as MainParent).loadingLayout.visibility = View.GONE
            (activity as MainParent).animator1.pause(); (activity as MainParent).animator2.pause()
            (activity as MainParent).animator3.pause(); (activity as MainParent).animator4.pause()

            increase.setOnClickListener {
                startActivity(Intent(requireContext(), increaseExposure::class.java)
                    .putExtra("productName", fundName.text.toString().trim())
                    .putExtra("username", (activity as MainParent).username)
                )
            }
            decrease.setOnClickListener {
                startActivity(Intent(requireContext(), decreaseExposure::class.java)
                    .putExtra("productName", fundName.text.toString().trim())
                    .putExtra("username", (activity as MainParent).username)
                )
            }

            seeFakeData.setOnClickListener {
                startActivity(Intent(requireContext(), raw_fake_data::class.java)
                    .putExtra("productName", fundName.text.toString().trim()))
            }
            (activity as MainParent).subcontext = ""
        }

        personalTransactionHistory.setOnClickListener {
            if ((activity as MainParent).username == "") {
                startActivity(Intent(requireContext(), login::class.java))
            } else {
                dialog.setContentView(R.layout.personal_transaction_history)
                dialog.show()

                val recyclerView = dialog.findViewById(R.id.recyclerView) as RecyclerView
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                val noRecord = dialog.findViewById(R.id.noRecord) as TextView
                val cross = dialog.findViewById(R.id.cross) as ImageView
                val transactionList: ArrayList<RecipientFindModel> = ArrayList()

                cross.setOnClickListener {
                    dialog.dismiss()
                }

                (activity as MainParent).loadingLayout.visibility = View.VISIBLE
                (activity as MainParent).animator1.start(); (activity as MainParent).animator2.start()
                (activity as MainParent).animator3.start(); (activity as MainParent).animator4.start()
                loadHistory(transactionList, noRecord)
                val adapter = RecipientFindAdapter(transactionList)
                recyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
                (activity as MainParent).loadingLayout.visibility = View.GONE
                (activity as MainParent).animator1.pause(); (activity as MainParent).animator2.pause()
                (activity as MainParent).animator3.pause(); (activity as MainParent).animator4.pause()
            }
        }
        totalActivity.setOnClickListener {
            if ((activity as MainParent).username == "") {
                startActivity(Intent(requireContext(), login::class.java))
            } else {
                dialog.setContentView(R.layout.personal_view)
                dialog.show()

                val noResult = dialog.findViewById(R.id.noResult) as TextView
                val cross = dialog.findViewById(R.id.cross) as ImageView
                val month1 = dialog.findViewById(R.id.month1) as TextView
                val month3 = dialog.findViewById(R.id.month3) as TextView
                val month6 = dialog.findViewById(R.id.month6) as TextView
                val monthAll = dialog.findViewById(R.id.monthAll) as TextView
                var selectedMonth = 1
                val transactionHistoryList: ArrayList<TransactionHistoryModel> = ArrayList()
                val recyclerView = dialog.findViewById(R.id.recyclerView) as RecyclerView
                recyclerView.layoutManager = LinearLayoutManager(requireContext())

                (activity as MainParent).loadingLayout.visibility = View.VISIBLE
                (activity as MainParent).animator1.start(); (activity as MainParent).animator2.start()
                (activity as MainParent).animator3.start(); (activity as MainParent).animator4.start()
                getPersonalHistory(noResult, selectedMonth, transactionHistoryList, recyclerView)
                val adapter = TransactionHistoryAdapter(transactionHistoryList)
                recyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
                (activity as MainParent).loadingLayout.visibility = View.GONE
                (activity as MainParent).animator1.pause(); (activity as MainParent).animator2.pause()
                (activity as MainParent).animator3.pause(); (activity as MainParent).animator4.pause()

                cross.setOnClickListener {
                    dialog.dismiss()
                }

                month1.setSafeOnClickListener {
                    if ((activity as MainParent).loadingLayout.visibility != View.VISIBLE) {
                        if (selectedMonth != 1) {
                            selectedMonth = 1
                            month1.background.setTint(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.selected_btn_background
                                )
                            )
                            month3.background.setTint(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.unselected_btn_background
                                )
                            )
                            month6.background.setTint(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.unselected_btn_background
                                )
                            )
                            monthAll.background.setTint(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.unselected_btn_background
                                )
                            )
                            month1.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.selected_btn_text
                                )
                            )
                            month3.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.unselected_btn_text
                                )
                            )
                            month6.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.unselected_btn_text
                                )
                            )
                            monthAll.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.unselected_btn_text
                                )
                            )
                            transactionHistoryList.clear()
                            (activity as MainParent).loadingLayout.visibility = View.VISIBLE
                            (activity as MainParent).animator1.start(); (activity as MainParent).animator2.start()
                            (activity as MainParent).animator3.start(); (activity as MainParent).animator4.start()
                            getPersonalHistory(
                                noResult,
                                selectedMonth,
                                transactionHistoryList,
                                recyclerView
                            )
                            val adapter = TransactionHistoryAdapter(transactionHistoryList)
                            recyclerView.adapter = adapter
                            adapter.notifyDataSetChanged()
                            (activity as MainParent).loadingLayout.visibility = View.GONE
                            (activity as MainParent).animator1.pause(); (activity as MainParent).animator2.pause()
                            (activity as MainParent).animator3.pause(); (activity as MainParent).animator4.pause()
                        }
                    }
                }
                month3.setSafeOnClickListener {
                    if ((activity as MainParent).loadingLayout.visibility != View.VISIBLE) {
                        if (selectedMonth != 3) {
                            selectedMonth = 3
                            month1.background.setTint(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.unselected_btn_background
                                )
                            )
                            month3.background.setTint(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.selected_btn_background
                                )
                            )
                            month6.background.setTint(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.unselected_btn_background
                                )
                            )
                            monthAll.background.setTint(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.unselected_btn_background
                                )
                            )
                            month1.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.unselected_btn_text
                                )
                            )
                            month3.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.selected_btn_text
                                )
                            )
                            month6.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.unselected_btn_text
                                )
                            )
                            monthAll.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.unselected_btn_text
                                )
                            )
                            transactionHistoryList.clear()
                            (activity as MainParent).loadingLayout.visibility = View.VISIBLE
                            (activity as MainParent).animator1.start(); (activity as MainParent).animator2.start()
                            (activity as MainParent).animator3.start(); (activity as MainParent).animator4.start()
                            getPersonalHistory(
                                noResult,
                                selectedMonth,
                                transactionHistoryList,
                                recyclerView
                            )
                            val adapter = TransactionHistoryAdapter(transactionHistoryList)
                            recyclerView.adapter = adapter
                            adapter.notifyDataSetChanged()
                            (activity as MainParent).loadingLayout.visibility = View.GONE
                            (activity as MainParent).animator1.pause(); (activity as MainParent).animator2.pause()
                            (activity as MainParent).animator3.pause(); (activity as MainParent).animator4.pause()
                        }
                    }
                }
                month6.setSafeOnClickListener {
                    if ((activity as MainParent).loadingLayout.visibility != View.VISIBLE) {
                        if (selectedMonth != 6) {
                            selectedMonth = 6
                            month1.background.setTint(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.unselected_btn_background
                                )
                            )
                            month3.background.setTint(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.unselected_btn_background
                                )
                            )
                            month6.background.setTint(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.selected_btn_background
                                )
                            )
                            monthAll.background.setTint(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.unselected_btn_background
                                )
                            )
                            month1.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.unselected_btn_text
                                )
                            )
                            month3.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.unselected_btn_text
                                )
                            )
                            month6.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.selected_btn_text
                                )
                            )
                            monthAll.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.unselected_btn_text
                                )
                            )
                            transactionHistoryList.clear()
                            (activity as MainParent).loadingLayout.visibility = View.VISIBLE
                            (activity as MainParent).animator1.start(); (activity as MainParent).animator2.start()
                            (activity as MainParent).animator3.start(); (activity as MainParent).animator4.start()
                            getPersonalHistory(
                                noResult,
                                selectedMonth,
                                transactionHistoryList,
                                recyclerView
                            )
                            val adapter = TransactionHistoryAdapter(transactionHistoryList)
                            recyclerView.adapter = adapter
                            adapter.notifyDataSetChanged()
                            (activity as MainParent).loadingLayout.visibility = View.GONE
                            (activity as MainParent).animator1.pause(); (activity as MainParent).animator2.pause()
                            (activity as MainParent).animator3.pause(); (activity as MainParent).animator4.pause()
                        }
                    }
                }
                monthAll.setSafeOnClickListener {
                    if ((activity as MainParent).loadingLayout.visibility != View.VISIBLE) {
                        if (selectedMonth != 100) {
                            selectedMonth = 100
                            month1.background.setTint(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.unselected_btn_background
                                )
                            )
                            month3.background.setTint(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.unselected_btn_background
                                )
                            )
                            month6.background.setTint(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.unselected_btn_background
                                )
                            )
                            monthAll.background.setTint(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.selected_btn_background
                                )
                            )
                            month1.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.unselected_btn_text
                                )
                            )
                            month3.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.unselected_btn_text
                                )
                            )
                            month6.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.unselected_btn_text
                                )
                            )
                            monthAll.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.selected_btn_text
                                )
                            )
                            transactionHistoryList.clear()
                            (activity as MainParent).loadingLayout.visibility = View.VISIBLE
                            (activity as MainParent).animator1.start(); (activity as MainParent).animator2.start()
                            (activity as MainParent).animator3.start(); (activity as MainParent).animator4.start()
                            getPersonalHistory(
                                noResult,
                                selectedMonth,
                                transactionHistoryList,
                                recyclerView
                            )
                            val adapter = TransactionHistoryAdapter(transactionHistoryList)
                            recyclerView.adapter = adapter
                            adapter.notifyDataSetChanged()
                            (activity as MainParent).loadingLayout.visibility = View.GONE
                            (activity as MainParent).animator1.pause(); (activity as MainParent).animator2.pause()
                            (activity as MainParent).animator3.pause(); (activity as MainParent).animator4.pause()
                        }
                    }
                }
            }
        }
        if ((activity as MainParent).announcement_id != "") {
            _announcement.visibility = View.VISIBLE
            announcement_title.text = (activity as MainParent).announcement_title
            _announcement.setOnClickListener {
                val intent = Intent(requireContext(), announcement::class.java)
                intent.putExtra("announcement_id", (activity as MainParent).announcement_id)
                intent.putExtra("announcement_title", (activity as MainParent).announcement_title)
                intent.putExtra("announcement_body", (activity as MainParent).announcement_body)
                startActivity(intent)
            }
        }
        baseCurrency.text = (activity as MainParent).baseCurrency
        totalAmount.text = "%,.2f".format((activity as MainParent).totalAmount.toFloat())
        cardType.text = (activity as MainParent).cardType
        withdraw.setOnClickListener {
            if ((activity as MainParent).username == "") {
                startActivity(Intent(requireContext(), login::class.java))
            } else {
                dialog.setContentView(R.layout.cancelfund)
                dialog.show()
                var radioButton = 0
                val klipWallet = dialog.findViewById(R.id.klipWallet) as View
                val customWallet = dialog.findViewById(R.id.customWallet) as View
                val coinWallet = dialog.findViewById(R.id.coinWallet) as EditText
                val waiting = dialog.findViewById(R.id.waiting) as TextView
                val cancel = dialog.findViewById(R.id.cancel) as TextView
                val yes = dialog.findViewById(R.id.yes) as TextView
                val no = dialog.findViewById(R.id.no) as TextView
                val cancelamount = dialog.findViewById(R.id.cancelamount) as EditText
                val browserLink = dialog.findViewById(R.id.browserLink) as TextView

                browserLink.setOnClickListener {
                    val url = "https://www.dongkye.tech/help-center/how-to-use-klip"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                }
                coinWallet.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        coinWallet.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.login_form_edittext_focused
                        )
                    } else {
                        coinWallet.background =
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.login_form_edittext
                            )
                    }
                }
                cancelamount.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        cancelamount.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.login_form_edittext_focused
                        )
                    } else {
                        cancelamount.background =
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.login_form_edittext
                            )
                    }
                }
                cancel.setOnClickListener {
                    dialog.dismiss()
                }
                klipWallet.setOnClickListener {
                    if (radioButton != 0) {
                        radioButton = 0
                        klipWallet.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.passcode_auth_bluebutton
                        )
                        customWallet.background =
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.passcode_auth_grey
                            )
                        coinWallet.isEnabled = false
                    }
                }
                customWallet.setOnClickListener {
                    if (radioButton != 1) {
                        radioButton = 1
                        customWallet.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.passcode_auth_bluebutton
                        )
                        klipWallet.background =
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.passcode_auth_grey
                            )
                        coinWallet.isEnabled = true
                    }
                }
                yes.setOnClickListener {
                    var goStop = 0
                    if (cancelamount.text.toString() == "") {
                        goStop = 1
                    } else if (cancelamount.text.toString().toInt() == 0) {
                        goStop = 1
                    } else if (cancelamount.text.toString().toInt() > 100) {
                        goStop = 1
                        cancelamount.error = "최대 입력값은 100입니다"
                    }
                    if (goStop == 0) {
                        if (radioButton == 0) {
                            val klip = Klip.getInstance(requireContext())
                            val bappInfo = BAppInfo("디지월드")
                            val req = AuthRequest()
                            val callback: KlipCallback<KlipResponse> =
                                object : KlipCallback<KlipResponse> {
                                    override fun onSuccess(res: KlipResponse) {
                                        val jsonObj = JSONObject(res.toString())
                                        request_key = jsonObj.getString("request_key")
                                        try {
                                            klip.request(request_key)
                                        } catch (e: KlipRequestException) {
                                            e.printStackTrace()
                                        }
                                    }

                                    override fun onFail(res: KlipErrorResponse) {}
                                }
                            try {
                                klip.prepare(req, bappInfo, callback)
                            } catch (e: KlipRequestException) {
                                e.printStackTrace()
                            }
                            yes.visibility = View.GONE
                            cancel.visibility = View.GONE
                            waiting.visibility = View.VISIBLE
                            val handler = Handler()
                            handler.postDelayed({
                                waiting.visibility = View.GONE
                                no.visibility = View.VISIBLE
                            }, 7000)
                        } else {
                            if (coinWallet.text.toString() != "") {
                                takeAccount(
                                    (activity as MainParent).username,
                                    coinWallet.text.toString(),
                                    cancelamount.text.toString(),
                                    dialog
                                )
                                no.visibility = View.GONE
                                cancel.text = "뒤로"
                                cancel.visibility = View.VISIBLE
                            }
                        }
                    }
                    no.setOnClickListener {
                        takeAccount(
                            (activity as MainParent).username,
                            request_key,
                            cancelamount.text.toString(),
                            dialog
                        )
                        no.visibility = View.GONE
                        cancel.text = "뒤로"
                        cancel.visibility = View.VISIBLE
                    }
                }
            }
        }
        deposit.setOnClickListener {
            if ((activity as MainParent).username == "") {
                startActivity(Intent(requireContext(), login::class.java))
            } else {
                dialog.setContentView(R.layout.investamount)
                dialog.show()
                val investamount = dialog.findViewById(R.id.investamount) as EditText
                val token = dialog.findViewById(R.id.token) as TextView
                val waiting = dialog.findViewById(R.id.waiting) as TextView
                val yes = dialog.findViewById(R.id.yes) as TextView
                val no = dialog.findViewById(R.id.no) as TextView
                val cancel = dialog.findViewById(R.id.cancel) as TextView
                val browserLink = dialog.findViewById(R.id.browserLink) as TextView

                browserLink.setOnClickListener {
                    val url = "https://www.dongkye.tech/help-center/how-to-use-klip"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                }
                cancel.setOnClickListener {
                    dialog.dismiss()
                }
                token.setOnClickListener {
                    if (token.text.toString() == "BORA") {
                        token.text = "SSX"
                    } else {
                        token.text = "BORA"
                    }
                }
                yes.setSafeOnClickListener {
                    var goStop = 0
                    val investamount_txt = investamount.text.toString()
                    if (investamount_txt.contains(".")) {
                        if (investamount_txt.split(".")[1].length > 6) {
                            investamount.error = "소수점 6자리까지만 반영됩니다"
                            goStop = 1
                        }
                    }
                    if (investamount_txt != "") {
                        val investamount_int = investamount.text.toString().toFloat()
                        if (token.text.toString() == "BORA" && investamount_int < 200) {
                            investamount.error = "최소 BORA 200개를 송금하세요"
                            goStop = 1
                        } else if (token.text.toString() == "SSX" && investamount_int < 2000) {
                            investamount.error = "최소 SSX 2천개를 송금하세요"
                            goStop = 1
                        }
                    } else {
                        goStop = 1
                    }
                    if (goStop == 0) {
                        lateinit var tokenSCA: String
                        lateinit var request_key: String
                        if (token.text.toString() == "SSX") {
                            tokenSCA = "0xdcd62c57182e780e23d2313c4782709da85b9d6c"
                        } else if (token.text.toString() == "BORA") {
                            tokenSCA = "0x02cbe46fb8a1f579254a9b485788f2d86cad51aa"
                        }
                        val bappInfo = BAppInfo("디지월드")
                        val klip = Klip.getInstance(requireContext())
                        val req = TokenTxRequest.Builder()
                            .contract(tokenSCA)
                            .to("0x071acad59bb6d86188ad993c810e0ab17ee56c7b")
                            .amount(investamount.text.toString())
                            .build()
                        val callback: KlipCallback<KlipResponse> =
                            object : KlipCallback<KlipResponse> {
                                override fun onSuccess(res: KlipResponse) {
                                    val jsonObj = JSONObject(res.toString())
                                    request_key = jsonObj.getString("request_key")
                                    try {
                                        klip.request(request_key)
                                    } catch (e: KlipRequestException) {
                                        e.printStackTrace()
                                    }
                                }

                                override fun onFail(res: KlipErrorResponse) {
                                    // Log.d("KLIP", res.toString());
                                }
                            }
                        try {
                            klip.prepare(req, bappInfo, callback)
                        } catch (e: KlipRequestException) {
                            e.printStackTrace();
                        }
                        yes.visibility = View.GONE
                        cancel.visibility = View.GONE
                        waiting.text = "잠시만 기다리세요.."
                        waiting.visibility = View.VISIBLE
                        val handler = Handler()
                        handler.postDelayed({
                            waiting.visibility = View.GONE
                            cancel.visibility = View.GONE
                            no.visibility = View.VISIBLE
                        }, 7000)
                        val callback1: KlipCallback<KlipResponse> =
                            object : KlipCallback<KlipResponse> {
                                override fun onSuccess(res: KlipResponse) {
                                    val result = res.toString()
                                    if (result.contains("completed")) {
                                        try {
                                            addAccount(
                                                (activity as MainParent).username,
                                                token.text.toString(),
                                                investamount.text.toString(),
                                                dialog
                                            )
                                        } catch (e: Exception) {
                                            val intent =
                                                Intent(requireContext(), KlipError::class.java)
                                            startActivity(intent)
                                        }
                                    } else {
                                        waiting.text = "앱이 종료되었습니다"
                                        waiting.visibility = View.VISIBLE
                                        no.visibility = View.GONE
                                        cancel.visibility = View.VISIBLE
                                    }
                                }

                                override fun onFail(res: KlipErrorResponse) {}
                            }
                        no.setSafeOnClickListener {
                            try {
                                klip.getResult(request_key, callback1)
                            } catch (e: KlipRequestException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        }

        if ((activity as MainParent).distribution == "") {
            totalDistribution.text = "0%"
            recyclerView.visibility = View.GONE
        } else {
            var _totalDistribution=0.0
            if ((activity as MainParent).distribution.contains("_")) {
                for (case in (activity as MainParent).distribution.split("_")) {
                    val subcase = case.split("&")
                    val newItem = distributionModel(subcase[0], subcase[1], subcase[2])
                    distributionList.add(newItem)
                    _totalDistribution+=subcase[2].toFloat()
                }
            } else {
                val subcase = (activity as MainParent).distribution.split("&")
                val newItem = distributionModel(subcase[0], subcase[1], subcase[2])
                distributionList.add(newItem)
                _totalDistribution+=subcase[2].toFloat()
            }
            totalDistribution.text = "%,.1f".format(_totalDistribution.toFloat())+"%"
            if (distributionList.size > 0) {
                val adapter = distributionAdapter(distributionList)
                recyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        }

        pieChart.description.isEnabled = false
        pieChart.isHighlightPerTapEnabled = false
        pieChart.isRotationEnabled = false
        pieChart.isDrawHoleEnabled = false
        pieChart.setDrawEntryLabels(false)
        pieChart.setExtraOffsets(0f, 0f, 15f, 0f)
        val NoOfEmp = ArrayList<PieEntry>()

        if ((activity as MainParent).balance == "" || (activity as MainParent).balance == "0") {
            NoOfEmp.add(PieEntry(1F, "없음"))
        } else {
            if ((activity as MainParent).balance.contains(",")) {
                for (index in (activity as MainParent).balance.split(",")) {
                    val value = if (index.contains(".")) {
                        index.split(".")[0]+"."+index.split(".")[1].filter { it.isDigit() }
                    } else {
                        index.filter { it.isDigit() }
                    }
                    val name = index.filter { it.isLetter() }
                    NoOfEmp.add(PieEntry(value.toFloat(), name))
                }
            } else {
                val value = if ((activity as MainParent).balance.contains(".")) {
                    (activity as MainParent).balance.split(".")[0]+"."+(activity as MainParent).balance.split(".")[1].filter { it.isDigit() }
                } else {
                    (activity as MainParent).balance.filter { it.isDigit() }
                }
                val name = (activity as MainParent).balance.filter { it.isLetter() }
                NoOfEmp.add(PieEntry(value.toFloat(), name))
            }
        }

        val dataSet = PieDataSet(NoOfEmp, "")
        dataSet.setDrawValues(false)

        val l: Legend = pieChart.legend
        l.verticalAlignment =
            Legend.LegendVerticalAlignment.CENTER
        l.horizontalAlignment =
            Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)

        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 0f
        dataSet.iconsOffset = MPPointF(0F, 40F)
        dataSet.selectionShift = 5f
        dataSet.setColors(intArrayOf(R.color.pie1, R.color.pie2, R.color.pie3, R.color.pie4, R.color.pie5, R.color.pie6, R.color.pie7, R.color.pie8), requireContext())

        dataSet.valueTextSize = 11f
        dataSet.valueTextColor = Color.WHITE

        val data = PieData(dataSet)

        if ((activity as MainParent).balance == "" || (activity as MainParent).balance == "0") {
            data.setDrawValues(false)
        } else {
            data.setDrawValues(true)
        }

        pieChart.data = data
        pieChart.highlightValues(null)
        pieChart.invalidate()

        return view
    }

    private fun getPersonalHistory(noResult: TextView, selectedMonth: Int, transactionHistoryList: ArrayList<TransactionHistoryModel>, recyclerView: RecyclerView) {
        if (noResult.visibility == View.VISIBLE) {
            noResult.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
        val mURL = URL("https://www.dongkye.tech/A5/getPersonalHistory.php")
        var reqParam = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode((activity as MainParent).username, "UTF-8")
        reqParam += "&" + URLEncoder.encode("selectedMonth", "UTF-8") + "=" + URLEncoder.encode(selectedMonth.toString(), "UTF-8")
        reqParam += "&" + URLEncoder.encode("receiver_id", "UTF-8") + "=" + URLEncoder.encode((activity as MainParent).receiver_id, "UTF-8")
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
                        val tempData = TransactionHistoryModel(
                            baseInfo.getString("activityType"),
                            baseInfo.getString("datetime"),
                            baseInfo.getString("amount"),
                            baseInfo.getString("token"),
                            baseInfo.getString("actionType")
                        )
                        transactionHistoryList.add(tempData)
                    }
                } else {
                    recyclerView.visibility = View.GONE
                    noResult.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun loadHistory(transactionList: ArrayList<RecipientFindModel>, noRecord: TextView) {
        val mURL = URL("https://www.dongkye.tech/A5/personal_transaction_history.php")
        var reqParam = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode((activity as MainParent).username, "UTF-8")
        reqParam += "&" + URLEncoder.encode("receiver_id", "UTF-8") + "=" + URLEncoder.encode((activity as MainParent).receiver_id, "UTF-8")
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
                    noRecord.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
            }
        }
    }
    inner class RecipientFindAdapter(private val mList: ArrayList<RecipientFindModel>) : RecyclerView.Adapter<RecipientFindAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.personal_transaction_history_recyclerview, parent, false)

            return ViewHolder(view)
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val ItemsViewModel = mList[position]

            holder.datetime.text = ItemsViewModel.datetime
            holder.address.text = ItemsViewModel.recipient + " : " + ItemsViewModel.toAddress
            if (ItemsViewModel.action == "1") {
                holder.amount.text = "+" + ItemsViewModel.actual_amount
                holder.amount.setTextColor(Color.parseColor("#800000"))
            } else {
                holder.amount.text = "-" + ItemsViewModel.actual_amount
                holder.amount.setTextColor(Color.parseColor("#000099"))
            }
            holder.token.text = ItemsViewModel.token
        }

        override fun getItemCount(): Int {
            return mList.size
        }

        inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
            val datetime: TextView = itemView.findViewById(R.id.datetime)
            val amount: TextView = itemView.findViewById(R.id.amount)
            val address: TextView = itemView.findViewById(R.id.timeonly)
            val token: TextView = itemView.findViewById(R.id.token)
        }
    }

    private fun takeAccount(username: String, address: String, amount: String, dialog: Dialog) {
        var reqParam = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8")
        reqParam += "&" + URLEncoder.encode("address", "UTF-8") + "=" + URLEncoder.encode(address, "UTF-8")
        reqParam += "&" + URLEncoder.encode("amount", "UTF-8") + "=" + URLEncoder.encode(amount, "UTF-8")
        reqParam += "&" + URLEncoder.encode("baseCurrency", "UTF-8") + "=" + URLEncoder.encode((activity as MainParent).baseCurrency, "UTF-8")
        val mURL = URL("https://www.dongkye.tech/A5/takeAccount.php")
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
                dialog.dismiss()
                if (jsonObj.getString("success") == "false") {
                    startActivity(Intent(requireContext(), KlipError::class.java))
                } else if (jsonObj.getString("success") == "true") {
                    startActivity(Intent(requireContext(), personal_view::class.java)
                        .putExtra("username", (activity as MainParent).username)
                        .putExtra("receiver_id", (activity as MainParent).receiver_id)
                    )
                }
            }
        }
    }

    private fun addAccount(username: String, token: String, investamount: String, dialog: Dialog) {
        var reqParam = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8")
        reqParam += "&" + URLEncoder.encode("token", "UTF-8") + "=" + URLEncoder.encode(token, "UTF-8")
        reqParam += "&" + URLEncoder.encode("investamount", "UTF-8") + "=" + URLEncoder.encode(investamount, "UTF-8")
        val mURL = URL("https://www.dongkye.tech/A5/addAccount.php")
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
                dialog.dismiss()
                if (jsonObj.getString("success") == "false") {
                    startActivity(Intent(requireContext(), KlipError::class.java))
                } else if (jsonObj.getString("success") == "true") {
                    startActivity(Intent(requireContext(), personal_view::class.java)
                        .putExtra("receiver_id", (activity as MainParent).receiver_id)
                        .putExtra("username", (activity as MainParent).username)
                    )
                }
            }
        }
    }

    private fun loadProductView(productName: String, productViewList: ArrayList<ProductViewModel>, total: TextView, average: TextView, noRecord: TextView, recyclerView: RecyclerView) {
        var reqParam = URLEncoder.encode("productName", "UTF-8") + "=" + URLEncoder.encode(productName, "UTF-8")
        reqParam += "&" + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode((activity as MainParent).username, "UTF-8")
        val mURL = URL("https://www.dongkye.tech/A5/personal_product_view.php")
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
                if (jsonArray.length() > 1) {
                    for (i in 0 until jsonArray.length()-1) {
                        val baseInfo = jsonArray.getJSONObject(i)
                        val tempData = ProductViewModel(
                            baseInfo.getString("date"),
                            baseInfo.getString("ticker"),
                            baseInfo.getString("pnl"),
                            baseInfo.getString("perc")
                        )
                        productViewList.add(tempData)
                    }
                    for (i in jsonArray.length()-1 until jsonArray.length()) {
                        val jsonObj = jsonArray.getJSONObject(i)
                        total.text = jsonObj.getString("total") + "원"
                        average.text = jsonObj.getString("average") + "%"
                    }
                } else {
                    noRecord.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
            }
        }
    }

    inner class distributionAdapter(
        private val mList: List<distributionModel>
    ) : RecyclerView.Adapter<distributionAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.main_recyclerview, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val ItemsViewModel = mList[position]
            holder.actualAmount.text = ItemsViewModel.perc
            holder.serviceName.text = ItemsViewModel.serviceName
            holder.distribution.text = ItemsViewModel.distributedAmount + "%"

            if (position == 0 && mList.size > 0) {
                holder.layout.setBackgroundResource(R.drawable.settings_item)
            } else if (position == mList.size-1 && mList.size > 0) {
                holder.layout.setBackgroundResource(R.drawable.settings_item_2)
            } else if (position > 0 && position < mList.size-1 && mList.size > 0) {
                holder.layout.setBackgroundResource(R.drawable.settings_item_3)
            } else {
                holder.layout.setBackgroundResource(R.drawable.settings_item_4)
            }

            holder.itemView.setOnClickListener {
                val dialog = Dialog(requireContext(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
                dialog.setCancelable(true)
                dialog.setContentView(R.layout.personal_product_view)
                dialog.show()

                val fundName = dialog.findViewById(R.id.fundName) as TextView
                val seeFakeData = dialog.findViewById(R.id.seeFakeData) as TextView
                val total = dialog.findViewById(R.id.total) as TextView
                val average = dialog.findViewById(R.id.average) as TextView
                val recyclerView = dialog.findViewById(R.id.recyclerView) as RecyclerView
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                val increase = dialog.findViewById(R.id.increase) as TextView
                val decrease = dialog.findViewById(R.id.decrease) as TextView
                val noRecord = dialog.findViewById(R.id.noRecord) as TextView
                val bottomNav = dialog.findViewById(R.id.bottomNav) as LinearLayout
                val productViewList: ArrayList<ProductViewModel> = ArrayList()

                (activity as MainParent).loadingLayout.visibility = View.VISIBLE
                (activity as MainParent).animator1.start(); (activity as MainParent).animator2.start()
                (activity as MainParent).animator3.start(); (activity as MainParent).animator4.start()

                fundName.text = holder.serviceName.text.toString().trim()

                if (fundName.text.toString().trim() == "포트폴리오") {
                    bottomNav.visibility = View.GONE
                    seeFakeData.visibility = View.GONE
                    getProductInfo_portfolio(total, average)
                    loadProductView_portfolio(productViewList, noRecord, recyclerView)
                } else {
                    loadProductView(holder.serviceName.text.toString().trim(), productViewList, total, average, noRecord, recyclerView)
                }
                val adapter = ProductViewAdapter(productViewList)
                recyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
                (activity as MainParent).loadingLayout.visibility = View.GONE
                (activity as MainParent).animator1.pause(); (activity as MainParent).animator2.pause()
                (activity as MainParent).animator3.pause(); (activity as MainParent).animator4.pause()

                increase.setOnClickListener {
                    startActivity(Intent(requireContext(), increaseExposure::class.java)
                        .putExtra("productName", fundName.text.toString().trim())
                        .putExtra("username", (activity as MainParent).username)
                    )
                }
                decrease.setOnClickListener {
                    startActivity(Intent(requireContext(), decreaseExposure::class.java)
                        .putExtra("productName", fundName.text.toString().trim())
                        .putExtra("username", (activity as MainParent).username)
                    )
                }

                seeFakeData.setOnClickListener {
                    startActivity(Intent(requireContext(), raw_fake_data::class.java)
                        .putExtra("productName", fundName.text.toString().trim()))
                }
            }
        }

        override fun getItemCount(): Int {
            return mList.size
        }

        inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
            val serviceName: TextView = itemView.findViewById(R.id.serviceName)
            val distribution: TextView = itemView.findViewById(R.id.distribution)
            val actualAmount: TextView = itemView.findViewById(R.id.actualAmount)
            val layout: LinearLayout = itemView.findViewById(R.id.layout)
        }
    }

    private fun getProductInfo_portfolio(total: TextView, average: TextView) {
        val mURL = URL("https://www.dongkye.tech/A5/personal_productInfo_portfolio.php")
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
                    val intent = Intent(requireContext(), ConnectionError::class.java)
                    startActivity(intent)
                } else if (jsonObj.getString("success") == "true") {
                    total.text = jsonObj.getString("total") + "BTC"
                    average.text = jsonObj.getString("average") + "%"
                }
            }
        }
    }
    private fun loadProductView_portfolio(productViewList: ArrayList<ProductViewModel>, noRecord: TextView, recyclerView: RecyclerView) {
        val reqParam = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode((activity as MainParent).username, "UTF-8")
        val mURL = URL("https://www.dongkye.tech/A5/personal_productView_portfolio.php")
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
                        val tempData = ProductViewModel(
                            baseInfo.getString("date"),
                            baseInfo.getString("ticker"),
                            baseInfo.getString("pnl"),
                            baseInfo.getString("perc")
                        )
                        productViewList.add(tempData)
                    }
                } else {
                    noRecord.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
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