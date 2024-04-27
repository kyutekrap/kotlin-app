package com.digiworld.kiwiv2

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.CycleInterpolator
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.math.BigInteger
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.security.MessageDigest

class edit_displayName : AppCompatActivity() {

    private lateinit var displayName: EditText
    private lateinit var password: EditText
    private lateinit var result_msg: TextView
    private lateinit var savebtn: TextView
    private var orig_displayName: String = ""
    private lateinit var loadingLayout: LinearLayout
    private lateinit var loading1: View
    private lateinit var loading2: View
    private lateinit var loading3: View
    private lateinit var loading4: View
    private lateinit var menuBurger: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_display_name)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val sharedPreference =  getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val username = sharedPreference.getString("username", "").toString()

        displayName = findViewById(R.id.displayName)
        password = findViewById(R.id.password)
        result_msg = findViewById(R.id.result_msg)
        savebtn = findViewById(R.id.savebtn)
        loadingLayout = findViewById(R.id.loadinglayout)
        loading1 = findViewById(R.id.loading1)
        loading2 = findViewById(R.id.loading2)
        loading3 = findViewById(R.id.loading3)
        loading4 = findViewById(R.id.loading4)
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

        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey("displayName")) {
                orig_displayName = extras.getString("displayName").toString()
                displayName.setText(extras.getString("displayName"))
                displayName.hint = extras.getString("displayName")
            }
        }

        savebtn.setSafeOnClickListener {
            if (loadingLayout.visibility != View.VISIBLE) {
                if (displayName.text.toString().trim() != "" && password.text.toString()
                        .trim() != ""
                ) {
                    if (displayName.text.toString().trim() != orig_displayName) {
                        try {
                            loadingLayout.visibility = View.VISIBLE
                            animator1.start(); animator2.start(); animator3.start(); animator4.start()
                            sendPostRequest(
                                displayName.text.toString(),
                                username,
                                password.text.toString(),
                                animator1,
                                animator2,
                                animator3,
                                animator4
                            )
                        } catch (e: Exception) {
                            // Log.d("Exception:", e.toString())
                        }
                    } else {
                        displayName.error = "변경사항이 없습니다"
                    }
                }
            }
        }

        displayName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                displayName.background = ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.login_form_edittext_focused
                )
            } else {
                displayName.background = ContextCompat.getDrawable(applicationContext, R.drawable.login_form_edittext)
            }
        }
        password.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                password.background = ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.login_form_edittext_focused
                )
            } else {
                password.background = ContextCompat.getDrawable(applicationContext, R.drawable.login_form_edittext)
            }
        }
    }

    fun sendPostRequest(displayName:String, username:String, password:String, animator1: ObjectAnimator, animator2: ObjectAnimator, animator3: ObjectAnimator, animator4: ObjectAnimator) {
        var reqParam = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8")
        reqParam += "&" + URLEncoder.encode("displayName", "UTF-8") + "=" + URLEncoder.encode(displayName, "UTF-8")
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
                    startActivity(Intent(applicationContext, MainParent::class.java)
                        .putExtra("context", "setting")
                    )
                } else {
                    loadingLayout.visibility = View.GONE
                    if (jsonObj.getString("success") == "false") {
                        result_msg.text = "네트워크 연결이 불안정합니다"
                        result_msg.visibility = View.VISIBLE
                    } else if (jsonObj.getString("success") == "wrong") {
                        result_msg.text = "비밀번호가 틀렸습니다"
                        result_msg.visibility = View.VISIBLE
                    }
                    animator1.pause(); animator2.pause(); animator3.pause(); animator4.pause()
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
}
