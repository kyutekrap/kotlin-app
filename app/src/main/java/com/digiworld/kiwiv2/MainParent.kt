package com.digiworld.kiwiv2

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.StrictMode
import android.provider.BaseColumns
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.animation.Animation
import android.view.animation.CycleInterpolator
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
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

class MainParent : AppCompatActivity() {

    private lateinit var pay: RelativeLayout
    private lateinit var asset: RelativeLayout
    private lateinit var service: RelativeLayout
    private lateinit var setting: RelativeLayout
    private lateinit var pay_img: ImageView
    private lateinit var asset_img: ImageView
    private lateinit var service_img: ImageView
    private lateinit var setting_img: ImageView
    private lateinit var view: FrameLayout
    var exposure: String = "0"
    var username: String = ""
    var baseCurrency: String = "KRW"
    var totalAmount: String = "0"
    var cardType: String = ""
    var cardlist: ArrayList<cardlist> = ArrayList()
    var announcement_id: String = ""
    var announcement_title: String = ""
    var announcement_body: String = ""
    var receiver_id: String = ""
    var displayName: String = ""
    var distribution: String = ""
    var balance: String = ""
    var last_date: String = ""
    private lateinit var bottomnav: LinearLayout
    private var random: Int = 0
    private var IntArray = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
    private var passCode: String = ""
    private var newCode: String = ""
    private var hideAuthentication: Int = 0
    lateinit var loadingLayout: LinearLayout
    lateinit var loading1: View
    lateinit var loading2: View
    lateinit var loading3: View
    lateinit var loading4: View
    lateinit var animator1: ObjectAnimator
    lateinit var animator2: ObjectAnimator
    lateinit var animator3: ObjectAnimator
    lateinit var animator4: ObjectAnimator
    var subcontext: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_parent)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        pay = findViewById(R.id.pay)
        asset = findViewById(R.id.asset)
        service = findViewById(R.id.service)
        setting = findViewById(R.id.setting)
        pay_img = findViewById(R.id.pay_img)
        asset_img = findViewById(R.id.asset_img)
        service_img = findViewById(R.id.service_img)
        setting_img = findViewById(R.id.setting_img)
        view = findViewById(R.id.view)
        bottomnav = findViewById(R.id.bottomNav)
        loadingLayout = findViewById(R.id.loadinglayout)
        loading1 = findViewById(R.id.loading1)
        loading2 = findViewById(R.id.loading2)
        loading3 = findViewById(R.id.loading3)
        loading4 = findViewById(R.id.loading4)

        animator1 = ObjectAnimator.ofFloat(loading1, "translationY", 50f)
        animator1.interpolator = CycleInterpolator(1f)
        animator1.duration = 2000

        animator2 = ObjectAnimator.ofFloat(loading2, "translationY", 50f)
        animator2.interpolator = CycleInterpolator(1f)
        animator2.duration = 2000
        animator2.startDelay = 200

        animator3 = ObjectAnimator.ofFloat(loading3, "translationY", 50f)
        animator3.interpolator = CycleInterpolator(1f)
        animator3.duration = 2000
        animator3.startDelay = 400

        animator4 = ObjectAnimator.ofFloat(loading4, "translationY", 50f)
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

        pay.alpha = 0.75f
        asset.alpha = 0.75f
        service.alpha = 0.75f
        setting.alpha = 0.75f

        val sharedPreference =  getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        username = sharedPreference.getString("username", "").toString()

        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey("context")) {
                if (loadingLayout.visibility != View.VISIBLE) {
                    loadingLayout.visibility = View.VISIBLE
                    animator1.start(); animator2.start(); animator3.start(); animator4.start()
                }
                hideAuthentication=1
                if (extras.getString("context") == "setting") {
                    try {
                        getUserInfo(username)
                        loadCardlist(username)
                        loadingLayout.visibility = View.GONE
                        animator1.pause(); animator2.pause(); animator3.pause(); animator4.pause()
                        bottomnav.visibility = View.VISIBLE
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.view, privacy_and_settings())
                            .commit()
                        asset_img.setImageResource(R.drawable.home_dark)
                    } catch (e: Exception) {
                        // Log.d("Error: ", e.toString())
                    }
                    service_img.setImageResource(R.drawable.menu)
                    asset_img.setImageResource(R.drawable.home)
                    pay_img.setImageResource(R.drawable.wallet)
                    setting_img.setImageResource(R.drawable.settings_dark)
                } else if (extras.getString("context") == "service") {
                    try {
                        getUserInfo(username)
                        loadCardlist(username)
                        loadingLayout.visibility = View.GONE
                        animator1.pause(); animator2.pause(); animator3.pause(); animator4.pause()
                        bottomnav.visibility = View.VISIBLE
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.view, services())
                            .commit()
                        asset_img.setImageResource(R.drawable.home_dark)
                    } catch (e: Exception) {
                        Log.d("Error: ", e.toString())
                    }
                    service_img.setImageResource(R.drawable.menu_dark)
                    asset_img.setImageResource(R.drawable.home)
                    pay_img.setImageResource(R.drawable.wallet)
                    setting_img.setImageResource(R.drawable.settings)
                } else if (extras.getString("context") == "main") {
                    if (extras.containsKey("subcontext")) {
                        subcontext = extras.getString("subcontext").toString()
                    }
                    try {
                        getUserInfo(username)
                        loadCardlist(username)
                        loadingLayout.visibility = View.GONE
                        animator1.pause(); animator2.pause(); animator3.pause(); animator4.pause()
                        bottomnav.visibility = View.VISIBLE
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.view, main())
                            .commit()
                        asset_img.setImageResource(R.drawable.home_dark)
                    } catch (e: Exception) {
                        Log.d("Error: ", e.toString())
                    }
                } else if (extras.getString("context") == "productChanged") {
                    try {
                        getUserInfo(username)
                        loadCardlist(username)
                        loadingLayout.visibility = View.GONE
                        animator1.pause(); animator2.pause(); animator3.pause(); animator4.pause()
                        bottomnav.visibility = View.VISIBLE
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.view, main())
                            .commit()
                        asset_img.setImageResource(R.drawable.home_dark)
                        subcontext = extras.getString("productName").toString()
                    } catch (e: Exception) {
                        Log.d("Error: ", e.toString())
                    }
                }
            } else {
                bottomnav.visibility = View.GONE
                supportFragmentManager.beginTransaction()
                    .replace(R.id.view, landing())
                    .commit()
            }
        } else {
            bottomnav.visibility = View.GONE
            supportFragmentManager.beginTransaction()
                .replace(R.id.view, landing())
                .commit()
        }

        //////////// PASSCODE AUTHENTICATION BEGINS HERE
        if (hideAuthentication==0) {
            val dialog = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.activity_passcode_auth)
            if (username != "") {
                dialog.show()
            } else {
                try {
                    // getUserInfo(username)
                    loadCardlist(username)
                    bottomnav.visibility = View.VISIBLE
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.view, main())
                        .commit()
                    asset_img.setImageResource(R.drawable.home_dark)
                } catch (e: Exception) {
                    Log.d("Error: ", e.toString())
                }
            }
            val passCode1 = dialog.findViewById(R.id.passCode1) as CardView
            val passCode2 = dialog.findViewById(R.id.passCode2) as CardView
            val passCode3 = dialog.findViewById(R.id.passCode3) as CardView
            val passCode4 = dialog.findViewById(R.id.passCode4) as CardView
            val forgotPin = dialog.findViewById(R.id.forgotPin) as TextView
            val number1 = dialog.findViewById(R.id.number1) as TextView
            val number2 = dialog.findViewById(R.id.number2) as TextView
            val number3 = dialog.findViewById(R.id.number3) as TextView
            val number4 = dialog.findViewById(R.id.number4) as TextView
            val number5 = dialog.findViewById(R.id.number5) as TextView
            val number6 = dialog.findViewById(R.id.number6) as TextView
            val number7 = dialog.findViewById(R.id.number7) as TextView
            val number8 = dialog.findViewById(R.id.number8) as TextView
            val number9 = dialog.findViewById(R.id.number9) as TextView
            val number0 = dialog.findViewById(R.id.number0) as TextView
            val del = dialog.findViewById(R.id.del) as TextView
            val createPin = dialog.findViewById(R.id.createPin) as TextView
            val wrong = dialog.findViewById(R.id.wrong) as TextView
            random = IntArray.random()
            number1.text = random.toString()
            IntArray = IntArray.filter{it!=random}
            random = IntArray.random()
            number2.text = random.toString()
            IntArray = IntArray.filter{it!=random}
            random = IntArray.random()
            number3.text = random.toString()
            IntArray = IntArray.filter{it!=random}
            random = IntArray.random()
            number4.text = random.toString()
            IntArray = IntArray.filter{it!=random}
            random = IntArray.random()
            number5.text = random.toString()
            IntArray = IntArray.filter{it!=random}
            random = IntArray.random()
            number6.text = random.toString()
            IntArray = IntArray.filter{it!=random}
            random = IntArray.random()
            number7.text = random.toString()
            IntArray = IntArray.filter{it!=random}
            random = IntArray.random()
            number8.text = random.toString()
            IntArray = IntArray.filter{it!=random}
            random = IntArray.random()
            number9.text = random.toString()
            IntArray = IntArray.filter{it!=random}
            random = IntArray.random()
            number0.text = random.toString()
            IntArray = IntArray.filter{it!=random}
            number1.setOnClickListener {
                if (passCode.length < 4) {
                    passCode+=number1.text.toString()
                }
                updatePasscode(wrong, forgotPin, passCode1, passCode2, passCode3, passCode4, createPin, "", dialog)
            }
            number2.setOnClickListener {
                if (passCode.length < 4) {
                    passCode+=number2.text.toString()
                }
                updatePasscode(wrong, forgotPin, passCode1, passCode2, passCode3, passCode4, createPin, "", dialog)
            }
            number3.setOnClickListener {
                if (passCode.length < 4) {
                    passCode+=number3.text.toString()
                }
                updatePasscode(wrong, forgotPin, passCode1, passCode2, passCode3, passCode4, createPin, "", dialog)
            }
            number4.setOnClickListener {
                if (passCode.length < 4) {
                    passCode+=number4.text.toString()
                }
                updatePasscode(wrong, forgotPin, passCode1, passCode2, passCode3, passCode4, createPin, "", dialog)
            }
            number5.setOnClickListener {
                if (passCode.length < 4) {
                    passCode+=number5.text.toString()
                }
                updatePasscode(wrong, forgotPin, passCode1, passCode2, passCode3, passCode4, createPin, "", dialog)
            }
            number6.setOnClickListener {
                if (passCode.length < 4) {
                    passCode+=number6.text.toString()
                }
                updatePasscode(wrong, forgotPin, passCode1, passCode2, passCode3, passCode4, createPin, "", dialog)
            }
            number7.setOnClickListener {
                if (passCode.length < 4) {
                    passCode+=number7.text.toString()
                }
                updatePasscode(wrong, forgotPin, passCode1, passCode2, passCode3, passCode4, createPin, "", dialog)
            }
            number8.setOnClickListener {
                if (passCode.length < 4) {
                    passCode+=number8.text.toString()
                }
                updatePasscode(wrong, forgotPin, passCode1, passCode2, passCode3, passCode4, createPin, "", dialog)
            }
            number9.setOnClickListener {
                if (passCode.length < 4) {
                    passCode+=number9.text.toString()
                }
                updatePasscode(wrong, forgotPin, passCode1, passCode2, passCode3, passCode4, createPin, "", dialog)
            }
            number0.setOnClickListener {
                if (passCode.length < 4) {
                    passCode+=number0.text.toString()
                }
                updatePasscode(wrong, forgotPin, passCode1, passCode2, passCode3, passCode4, createPin, "", dialog)
            }
            del.setOnClickListener {
                passCode = passCode.dropLast(1)
                updatePasscode(wrong, forgotPin, passCode1, passCode2, passCode3, passCode4, createPin, "", dialog)
            }
            passCode1.setBackgroundResource(R.drawable.passcode_auth_white)
            passCode2.setBackgroundResource(R.drawable.passcode_auth_white)
            passCode3.setBackgroundResource(R.drawable.passcode_auth_white)
            passCode4.setBackgroundResource(R.drawable.passcode_auth_white)
            val dbHelper = FeedReaderDbHelper(this)
            val db = dbHelper.readableDatabase
            val projection = arrayOf(BaseColumns._ID, FeedReaderDbHelper.FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE)
            val cursor = db.query(
                FeedReaderDbHelper.FeedReaderContract.FeedEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,          // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,
                null,
                null
            )
            val itemIds = mutableListOf<Long>()
            with(cursor) {
                while (moveToNext()) {
                    val itemId = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                    itemIds.add(itemId)
                }
            }
            cursor.close()
            if (itemIds.size == 0) {
                createPin.text = "PIN 암호를 생성하세요"
            } else {
                forgotPin.visibility = View.VISIBLE
            }
            forgotPin.setSafeOnClickListener {
                val db = dbHelper.writableDatabase
                db.execSQL("DELETE FROM ${FeedReaderDbHelper.FeedReaderContract.FeedEntry.TABLE_NAME}");
                val sharedPreferences = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.clear()
                editor.apply()
                startActivity(Intent(this, login::class.java))
            }
        }
        //////////// PASSCODE AUTHENTICATION ENDS HERE

        pay.setOnClickListener {
            if (username == "") {
                startActivity(Intent(this, login::class.java))
            } else {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.view, pay())
                    .commit()
                pay_img.setImageResource(R.drawable.wallet_dark)
                asset_img.setImageResource(R.drawable.home)
                service_img.setImageResource(R.drawable.menu)
                setting_img.setImageResource(R.drawable.settings)
            }
        }
        asset.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.view, main())
                .commit()
            asset_img.setImageResource(R.drawable.home_dark)
            pay_img.setImageResource(R.drawable.wallet)
            service_img.setImageResource(R.drawable.menu)
            setting_img.setImageResource(R.drawable.settings)
        }
        service.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.view, services())
                .commit()
            service_img.setImageResource(R.drawable.menu_dark)
            asset_img.setImageResource(R.drawable.home)
            pay_img.setImageResource(R.drawable.wallet)
            setting_img.setImageResource(R.drawable.settings)
        }
        setting.setOnClickListener {
            if (username == "") {
                startActivity(Intent(this, login::class.java))
            } else {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.view, privacy_and_settings())
                    .commit()
                service_img.setImageResource(R.drawable.menu)
                asset_img.setImageResource(R.drawable.home)
                pay_img.setImageResource(R.drawable.wallet)
                setting_img.setImageResource(R.drawable.settings_dark)
            }
        }
    }

    fun loadCardlist(username: String) {
        val mURL = URL("https://www.dongkye.tech/A5/getcard-All.php")
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
                        val tempData = cardlist(
                            baseInfo.getString("fundName"),
                            baseInfo.getString("fundMarket"),
                            baseInfo.getLong("fundQuota"),
                            baseInfo.getString("fundPosition"),
                            baseInfo.getLong("consumption"),
                            baseInfo.getString("description"),
                            baseInfo.getString("EAR"),
                            baseInfo.getLong("MDD"),
                            baseInfo.getString("underlyingAsset"),
                            baseInfo.getString("investCheck")
                        )
                        cardlist.add(tempData)
                    }
                }
            }
        }
    }

    private fun getUserInfo(s: String) {
        val reqParam = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(s, "UTF-8")
        val mURL = URL("https://www.dongkye.tech/A5/get_user_info.php")
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
                    baseCurrency = jsonObj.getString("baseCurrency")
                    totalAmount = jsonObj.getString("totalAmount")
                    distribution = jsonObj.getString("getDistribution")
                    balance = jsonObj.getString("balance")
                    receiver_id = jsonObj.getString("receiver_id")
                    displayName = jsonObj.getString("displayName")
                    exposure = jsonObj.getString("exposure")
                    if (jsonObj.has("last_date")) {
                        last_date = jsonObj.getString("last_date")
                    }
                    if (jsonObj.has("announcement_id")) {
                        announcement_id = jsonObj.getString("announcement_id")
                        announcement_title = jsonObj.getString("announcement_title")
                        announcement_body = jsonObj.getString("announcement_body")
                    }
                    if (jsonObj.has("suspicious_user")) {
                        if (jsonObj.getString("suspicious_user").contains(",")) {
                            val list: List<String> =
                                jsonObj.getString("suspicious_user").split(",").toList()
                            if (list.contains(username)) {
                                val dialog = Dialog(this@MainParent, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                                dialog.setCancelable(false)
                                dialog.setContentView(R.layout.account_ban)
                                dialog.show()
                            }
                        } else if (jsonObj.getString("suspicious_user") == username) {
                            val dialog = Dialog(this@MainParent, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                            dialog.setCancelable(false)
                            dialog.setContentView(R.layout.account_ban)
                            dialog.show()
                        }
                    }
                    if (jsonObj.has("frozen_users")) {
                        if (jsonObj.getString("frozen_users").contains(",")) {
                            val list: List<String> =
                                jsonObj.getString("frozen_users").split(",").toList()
                            if (list.contains(username)) {
                                val dialog = Dialog(this@MainParent, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                                dialog.setCancelable(false)
                                dialog.setContentView(R.layout.activity_account_frozen)
                                dialog.show()
                            }
                        } else if (jsonObj.getString("frozen_users") == username) {
                            val dialog = Dialog(this@MainParent, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                            dialog.setCancelable(false)
                            dialog.setContentView(R.layout.activity_account_frozen)
                            dialog.show()
                        }
                    }
                } else {
                    startActivity(Intent(applicationContext, ConnectionError::class.java))
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
    private fun String.sha256(): String {
        val md = MessageDigest.getInstance("SHA-256")
        return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
    }
    private fun authenticate(wrong:TextView,forgotPin:TextView,dialog:Dialog) {
        val dbHelper = FeedReaderDbHelper(this)
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
            BaseColumns._ID,
            FeedReaderDbHelper.FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE
        )
        val selection =
            "${FeedReaderDbHelper.FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE} = ?"
        val selectionArgs = arrayOf(passCode.sha256())
        val cursor = db.query(
            FeedReaderDbHelper.FeedReaderContract.FeedEntry.TABLE_NAME,   // The table to query
            projection,             // The array of columns to return (pass null to get all)
            selection,              // The columns for the WHERE clause
            selectionArgs,          // The values for the WHERE clause
            null,
            null,
            null
        )
        val itemIds = mutableListOf<Long>()
        with(cursor) {
            while (moveToNext()) {
                val itemId = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                itemIds.add(itemId)
            }
        }
        cursor.close()
        if (itemIds.size > 0) {
            dialog.dismiss()
            try {
                getUserInfo(username)
                loadCardlist(username)
                bottomnav.visibility = View.VISIBLE
                supportFragmentManager.beginTransaction()
                    .replace(R.id.view, main())
                    .commit()
                asset_img.setImageResource(R.drawable.home_dark)
            } catch (e: Exception) {
                Log.d("Error: ", e.toString())
            }
        } else {
            wrong.visibility = View.VISIBLE
            forgotPin.visibility = View.GONE
        }
    }
    private fun updatePasscode(wrong: TextView, forgotPin:TextView, passCode1:CardView, passCode2:CardView, passCode3:CardView, passCode4:CardView, createPin:TextView, newPin:String, dialog: Dialog) {
        if (passCode.isEmpty() && wrong.visibility == View.VISIBLE) {
            wrong.visibility = View.GONE
            forgotPin.visibility = View.VISIBLE
        }
        if (passCode.isNotEmpty()) {
            passCode1.setBackgroundResource(R.drawable.passcode_auth_black)
        } else {
            passCode1.setBackgroundResource(R.drawable.passcode_auth_white)
        }
        if (passCode.length > 1) {
            passCode2.setBackgroundResource(R.drawable.passcode_auth_black)
        } else {
            passCode2.setBackgroundResource(R.drawable.passcode_auth_white)
        }
        if (passCode.length > 2) {
            passCode3.setBackgroundResource(R.drawable.passcode_auth_black)
        } else {
            passCode3.setBackgroundResource(R.drawable.passcode_auth_white)
        }
        if (passCode.length > 3) {
            passCode4.setBackgroundResource(R.drawable.passcode_auth_black)
        } else {
            passCode4.setBackgroundResource(R.drawable.passcode_auth_white)
        }
        if (passCode.length == 4) {
            if (createPin.text.toString().trim() == "PIN 암호를 생성하세요") {
                createPin.text = "한번 더 입력하세요"
                newCode = passCode
                passCode=""
                updatePasscode(wrong, forgotPin, passCode1, passCode2, passCode3, passCode4, createPin, newCode, dialog)
                if (wrong.visibility == View.VISIBLE) {
                    wrong.visibility = View.GONE
                    forgotPin.visibility = View.INVISIBLE
                }
            } else if (createPin.text.toString().trim() == "한번 더 입력하세요") {
                if (newCode == passCode) {
                    val dbHelper = FeedReaderDbHelper(this)
                    val db = dbHelper.writableDatabase
                    val values = ContentValues().apply {
                        put(FeedReaderDbHelper.FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, passCode.sha256())
                    }
                    val newRowId = db?.insert(FeedReaderDbHelper.FeedReaderContract.FeedEntry.TABLE_NAME, null, values)
                    if (newRowId?.toInt() == -1) {
                        wrong.text = "다시 시도해주세요\n재실패 시 호환되지 않는 기기입니다"
                        wrong.visibility = View.VISIBLE
                        forgotPin.visibility = View.GONE
                    } else {
                        authenticate(wrong, forgotPin, dialog)
                    }
                } else {
                    createPin.text = "PIN 암호를 생성하세요"
                    passCode=""
                    newCode=""
                    updatePasscode(wrong, forgotPin, passCode1, passCode2, passCode3, passCode4, createPin, "", dialog)
                    wrong.visibility = View.VISIBLE
                }
            } else {
                authenticate(wrong, forgotPin, dialog)
            }
        }
    }
}