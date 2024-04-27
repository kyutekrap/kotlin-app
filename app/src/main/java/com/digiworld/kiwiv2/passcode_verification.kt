package com.digiworld.kiwiv2

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.StrictMode
import android.provider.BaseColumns
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.math.BigInteger
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.security.MessageDigest

class passcode_verification : AppCompatActivity() {

    private lateinit var forgotPin: TextView
    private lateinit var number1: TextView
    private lateinit var number2: TextView
    private lateinit var number3: TextView
    private lateinit var number4: TextView
    private lateinit var number5: TextView
    private lateinit var number6: TextView
    private lateinit var number7: TextView
    private lateinit var number8: TextView
    private lateinit var number9: TextView
    private lateinit var number0: TextView
    private lateinit var del: TextView
    private var IntArray = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
    private var random: Int = 0
    private var passCode: String = ""
    private lateinit var passCode1: CardView
    private lateinit var passCode2: CardView
    private lateinit var passCode3: CardView
    private lateinit var passCode4: CardView
    private lateinit var createPin: TextView
    private lateinit var wrong: TextView
    private var total: String = ""
    private var recipient: String = ""
    private var username: String = ""
    private var recipient_address: String = ""
    private var baseCurrency: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passcode_verification)

        forgotPin = findViewById(R.id.forgotPin)
        number1 = findViewById(R.id.number1)
        number2 = findViewById(R.id.number2)
        number3 = findViewById(R.id.number3)
        number4 = findViewById(R.id.number4)
        number5 = findViewById(R.id.number5)
        number6 = findViewById(R.id.number6)
        number7 = findViewById(R.id.number7)
        number8 = findViewById(R.id.number8)
        number9 = findViewById(R.id.number9)
        number0 = findViewById(R.id.number0)
        del = findViewById(R.id.del)
        createPin = findViewById(R.id.createPin)
        wrong = findViewById(R.id.wrong)
        passCode1 = findViewById(R.id.passCode1)
        passCode2 = findViewById(R.id.passCode2)
        passCode3 = findViewById(R.id.passCode3)
        passCode4 = findViewById(R.id.passCode4)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

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
        if (itemIds.size == 0) {
            startActivity(Intent(this, login::class.java))
        } else {
            forgotPin.visibility = View.VISIBLE
        }
        cursor.close()

        forgotPin.setSafeOnClickListener {
            val db = dbHelper.writableDatabase
            db.execSQL("DELETE FROM ${FeedReaderDbHelper.FeedReaderContract.FeedEntry.TABLE_NAME}");
            val sharedPreferences = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()
            startActivity(Intent(this, login::class.java))
        }

        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey("total")) {
                total = extras.getString("total").toString()
            }
            if (extras.containsKey("recipient")) {
                recipient = extras.getString("recipient").toString()
            }
            if (extras.containsKey("username")) {
                username = extras.getString("username").toString()
            }
            if (extras.containsKey("recipient_address")) {
                recipient_address = extras.getString("recipient_address").toString()
            }
            if (extras.containsKey("baseCurrency")) {
                baseCurrency = extras.getString("baseCurrency").toString()
            }
        }

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
            updatePasscode()
        }
        number2.setOnClickListener {
            if (passCode.length < 4) {
                passCode+=number2.text.toString()
            }
            updatePasscode()
        }
        number3.setOnClickListener {
            if (passCode.length < 4) {
                passCode+=number3.text.toString()
            }
            updatePasscode()
        }
        number4.setOnClickListener {
            if (passCode.length < 4) {
                passCode+=number4.text.toString()
            }
            updatePasscode()
        }
        number5.setOnClickListener {
            if (passCode.length < 4) {
                passCode+=number5.text.toString()
            }
            updatePasscode()
        }
        number6.setOnClickListener {
            if (passCode.length < 4) {
                passCode+=number6.text.toString()
            }
            updatePasscode()
        }
        number7.setOnClickListener {
            if (passCode.length < 4) {
                passCode+=number7.text.toString()
            }
            updatePasscode()
        }
        number8.setOnClickListener {
            if (passCode.length < 4) {
                passCode+=number8.text.toString()
            }
            updatePasscode()
        }
        number9.setOnClickListener {
            if (passCode.length < 4) {
                passCode+=number9.text.toString()
            }
            updatePasscode()
        }
        number0.setOnClickListener {
            if (passCode.length < 4) {
                passCode+=number0.text.toString()
            }
            updatePasscode()
        }
        del.setOnClickListener {
            passCode = passCode.dropLast(1)
            updatePasscode()
        }
        passCode1.setBackgroundResource(R.drawable.passcode_auth_grey)
        passCode2.setBackgroundResource(R.drawable.passcode_auth_grey)
        passCode3.setBackgroundResource(R.drawable.passcode_auth_grey)
        passCode4.setBackgroundResource(R.drawable.passcode_auth_grey)
    }

    private fun authenticate() {
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
            runTransaction()
        } else {
            wrong.visibility = View.VISIBLE
            forgotPin.visibility = View.GONE
        }
    }

    private fun runTransaction() {
        var reqParam = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8")
        reqParam += "&" + URLEncoder.encode("total", "UTF-8") + "=" + URLEncoder.encode(total, "UTF-8")
        reqParam += "&" + URLEncoder.encode("recipient", "UTF-8") + "=" + URLEncoder.encode(recipient, "UTF-8")
        reqParam += "&" + URLEncoder.encode("recipient_address", "UTF-8") + "=" + URLEncoder.encode(recipient_address, "UTF-8")
        reqParam += "&" + URLEncoder.encode("baseCurrency", "UTF-8") + "=" + URLEncoder.encode(baseCurrency, "UTF-8")
        val mURL = URL("https://www.dongkye.tech/A5/runTransaction.php")
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
                    wrong.text = "네트워크 연결이 불안정합니다"
                    wrong.visibility = View.VISIBLE
                    forgotPin.visibility = View.GONE
                } else if (jsonObj.getString("success") == "true") {
                    startActivity(Intent(applicationContext, MainParent::class.java)
                        .putExtra("context", "main")
                        .putExtra("subcontext", jsonObj.getString("personal_transactions"))
                    )
                } else if (jsonObj.getString("success")=="insufficient") {
                    wrong.text = "잔금이 부족합니다"
                    wrong.visibility = View.VISIBLE
                    forgotPin.visibility = View.GONE
                }
            }

        }
    }

    private fun updatePasscode() {
        if (passCode.isEmpty() && wrong.visibility == View.VISIBLE) {
            wrong.visibility = View.GONE
            forgotPin.visibility = View.VISIBLE
        }
        if (passCode.isNotEmpty()) {
            passCode1.setBackgroundResource(R.drawable.passcode_auth_black)
        } else {
            passCode1.setBackgroundResource(R.drawable.passcode_auth_grey)
        }
        if (passCode.length > 1) {
            passCode2.setBackgroundResource(R.drawable.passcode_auth_black)
        } else {
            passCode2.setBackgroundResource(R.drawable.passcode_auth_grey)
        }
        if (passCode.length > 2) {
            passCode3.setBackgroundResource(R.drawable.passcode_auth_black)
        } else {
            passCode3.setBackgroundResource(R.drawable.passcode_auth_grey)
        }
        if (passCode.length > 3) {
            passCode4.setBackgroundResource(R.drawable.passcode_auth_black)
        } else {
            passCode4.setBackgroundResource(R.drawable.passcode_auth_grey)
        }
        if (passCode.length == 4) {
            authenticate()
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
}