package com.digiworld.kiwiv2

import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.*
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.activity.OnBackPressedDispatcher
import androidx.annotation.ColorRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class services : Fragment() {

    private lateinit var viewPager: ViewPager2
    val bgColors: MutableList<Int> = mutableListOf(
        R.color.card1,
        R.color.card2
    )
    private lateinit var totalAmount: TextView
    private lateinit var exposure: TextView
    private lateinit var estimateAmount: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var savebtn: TextView
    private var currentProgress: Int = 0
    private var cardlist: ArrayList<cardlist> = ArrayList()
    private lateinit var investBTC: LinearLayout
    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.services, container, false)

        dialog = Dialog(requireContext(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
        dialog.setCancelable(true)

        totalAmount = view.findViewById(R.id.totalAmount)
        exposure = view.findViewById(R.id.exposure)
        estimateAmount = view.findViewById(R.id.estimateAmount)
        seekBar = view.findViewById(R.id.seekBar)
        savebtn = view.findViewById(R.id.savebtn)
        investBTC = view.findViewById(R.id.investBTC)


        if ((activity as MainParent).balance == "0") {
            totalAmount.text = "보유코인 없음"
            exposure.text = "0%"
            estimateAmount.text = ""
        } else {
            totalAmount.text = "%,.2f".format((activity as MainParent).totalAmount.toFloat())
            exposure.text = (activity as MainParent).exposure + "%"
        }
        var estimate_amount = ((activity as MainParent).exposure.toFloat() / 100) * (activity as MainParent).totalAmount.toFloat()
        estimateAmount.text = "%,.2f".format(estimate_amount)

        seekBar.progress = (activity as MainParent).exposure.toInt()

        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if ((activity as MainParent).exposure.toInt() != currentProgress) {
                    investBTC.visibility = View.VISIBLE
                } else {
                    investBTC.visibility = View.GONE
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                exposure.text = progress.toString() + "%"
                estimate_amount = (progress.toFloat() / 100) * (activity as MainParent).totalAmount.toFloat()
                estimateAmount.text = "%,.2f".format(estimate_amount)
                currentProgress = progress
            }
        })
        savebtn.setSafeOnClickListener {
            if ((activity as MainParent).loadingLayout.visibility != View.VISIBLE) {
                if ((activity as MainParent).username == "") {
                    startActivity(Intent(requireContext(), login::class.java))
                } else {
                    (activity as MainParent).loadingLayout.visibility = View.VISIBLE
                    (activity as MainParent).animator1.start()
                    (activity as MainParent).animator2.start()
                    (activity as MainParent).animator3.start()
                    (activity as MainParent).animator4.start()
                    registerService()
                }
            }
        }
        viewPager = view.findViewById(R.id.viewPager)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        cardlist = (activity as MainParent).cardlist
        if (cardlist.size > 0) {
            val _adapter = CustomPagerAdapter()
            viewPager.adapter = _adapter
        }
        return view
    }

    private fun registerService() {
        val mURL = URL("https://www.dongkye.tech/A5/registerService.php")
        var reqParam = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode((activity as MainParent).username, "UTF-8")
        reqParam += "&" + URLEncoder.encode("progress", "UTF-8") + "=" + URLEncoder.encode(currentProgress.toString(), "UTF-8")
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
                    val builder = AlertDialog.Builder(ContextThemeWrapper(requireContext(), R.style.MyAlertDialogTheme))
                    builder.setTitle("네트워크 연결이 불안정합니다")
                    val dialog = builder.create()
                    dialog.show()
                } else if (jsonObj.getString("success") == "true") {
                    startActivity(Intent(requireContext(), MainParent::class.java)
                        .putExtra("context", "service")
                    )
                }
            }
        }
    }

    inner class CustomPagerAdapter: RecyclerView.Adapter<CustomPagerAdapter.MyPagerViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPagerViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.services_recyclerview, parent, false)
            return MyPagerViewHolder(view)
        }
        override fun onBindViewHolder(holder: MyPagerViewHolder, position: Int) {
            holder.bind(bgColors[position], position)
        }
        override fun getItemCount(): Int {
            return bgColors.size
        }
        inner class MyPagerViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
            private val fundName : TextView = itemView.findViewById(R.id.fundName)
            private val MDD : TextView = itemView.findViewById(R.id.MDD)
            private val EAR : TextView = itemView.findViewById(R.id.EAR)
            private val underlyingAsset : TextView = itemView.findViewById(R.id.underlyingAsset)
            private val description : TextView = itemView.findViewById(R.id.description)
            private val fundQuota : TextView = itemView.findViewById(R.id.fundQuota)
            private val consumedQuota : RelativeLayout = itemView.findViewById(R.id.consumedQuota)
            private val linearLayout : LinearLayout = itemView.findViewById(R.id.linearLayout)
            private val cardView : LinearLayout = itemView.findViewById(R.id.cardview)
            fun bind(@ColorRes bgColor: Int, position: Int) {
                fundName.text = cardlist[position].fundName
                MDD.text = "투자비중 " + cardlist[position].MDD.toString() + "%"
                EAR.text = cardlist[position].EAR + "%"
                underlyingAsset.text = cardlist[position].underlyingAsset
                description.text = cardlist[position].description
                fundQuota.text = "운용상한선 " + cardlist[position].fundQuota + "백만원"
                cardView.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, bgColor))
                linearLayout.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, bgColor))
                val consumed = cardlist[position].consumption / cardlist[position].fundQuota
                if (consumed <= 0.05) {
                    (consumedQuota.layoutParams as LinearLayout.LayoutParams).weight = 0.1F
                } else if (consumed >= 1) {
                    (consumedQuota.layoutParams as LinearLayout.LayoutParams).weight = 1F
                } else {
                    (consumedQuota.layoutParams as LinearLayout.LayoutParams).weight = consumed.toFloat()
                }
                cardView.setOnClickListener {
                    dialog.setContentView(R.layout.product_view)
                    dialog.show()

                    val fundName = dialog.findViewById(R.id.fundName) as TextView
                    val seeFakeData = dialog.findViewById(R.id.seeFakeData) as TextView
                    val total = dialog.findViewById(R.id.total) as TextView
                    val average = dialog.findViewById(R.id.average) as TextView
                    val _newInvestment = dialog.findViewById(R.id.newInvestment) as TextView
                    val productViewList: ArrayList<ProductViewModel> = ArrayList()
                    val recyclerView = dialog.findViewById(R.id.recyclerView) as RecyclerView
                    recyclerView.layoutManager = LinearLayoutManager(requireContext())

                    fundName.text = cardlist[position].fundName

                    seeFakeData.setOnClickListener {
                        startActivity(Intent(requireContext(), raw_fake_data::class.java).putExtra("productName", fundName.text.toString().trim()))
                    }

                    if (cardlist[position].investCheck == "true") {
                        _newInvestment.text = "투자상환"
                        _newInvestment.setTextColor(Color.parseColor("#ffff00"))
                    } else {
                        if (consumed >= 1) {
                            _newInvestment.text = "더 이상 투자할 수 없습니다"
                        }
                    }

                    (activity as MainParent).loadingLayout.visibility = View.VISIBLE
                    (activity as MainParent).animator1.start(); (activity as MainParent).animator2.start()
                    (activity as MainParent).animator3.start(); (activity as MainParent).animator4.start()
                    loadProductView(fundName.text.toString().trim(), productViewList, total, average)
                    val adapter = ProductViewAdapter(productViewList)
                    recyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()
                    (activity as MainParent).loadingLayout.visibility = View.GONE
                    (activity as MainParent).animator1.pause(); (activity as MainParent).animator2.pause()
                    (activity as MainParent).animator3.pause(); (activity as MainParent).animator4.pause()

                    _newInvestment.setOnClickListener {
                        if ((activity as MainParent).username == "") {
                            startActivity(Intent(requireContext(), login::class.java))
                        } else {
                            if (_newInvestment.text.toString().trim() == "신규투자") {
                                startActivity(Intent(requireContext(), newInvestment::class.java)
                                    .putExtra("productName", fundName.text.toString().trim())
                                )
                            } else if (_newInvestment.text.toString().trim() == "투자상환") {
                                startActivity(Intent(requireContext(), verifyWithdrawal::class.java).putExtra("productName", fundName.text.toString().trim()))
                            }
                        }
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
    private fun loadProductView(productName: String, productViewList: ArrayList<ProductViewModel>, total: TextView, average: TextView) {
        val reqParam = URLEncoder.encode("productName", "UTF-8") + "=" + URLEncoder.encode(productName, "UTF-8")
        val mURL = URL("https://www.dongkye.tech/A5/product_view.php")
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
                        total.text = jsonObj.getString("total")
                        average.text = jsonObj.getString("average")
                    }
                }
            }
        }
    }
}