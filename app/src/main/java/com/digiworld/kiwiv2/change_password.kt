package com.digiworld.kiwiv2

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.graphics.Color
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

class change_password : AppCompatActivity() {

    private lateinit var original_password: EditText
    private lateinit var new_password: EditText
    private lateinit var check_password: EditText
    private lateinit var save_btn: TextView
    private lateinit var result_msg: TextView
    private var username: String = ""
    private lateinit var loadingLayout: LinearLayout
    private lateinit var loading1: View
    private lateinit var loading2: View
    private lateinit var loading3: View
    private lateinit var loading4: View
    private lateinit var menuBurger: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey("username")) {
                username = extras.getString("username").toString()
            }
        }

        menuBurger = findViewById(R.id.menu_burger)
        original_password = findViewById(R.id.original_password)
        new_password = findViewById(R.id.new_password)
        check_password = findViewById(R.id.check_password)
        save_btn = findViewById(R.id.savebtn)
        result_msg = findViewById(R.id.result_msg)
        loadingLayout = findViewById(R.id.loadinglayout)
        loading1 = findViewById(R.id.loading1)
        loading2 = findViewById(R.id.loading2)
        loading3 = findViewById(R.id.loading3)
        loading4 = findViewById(R.id.loading4)

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

        original_password.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                original_password.background = ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.login_form_edittext_focused
                )
            } else {
                original_password.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.login_form_edittext)
            }
        }
        new_password.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                new_password.background = ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.login_form_edittext_focused
                )
            } else {
                new_password.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.login_form_edittext)
            }
        }
        check_password.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                check_password.background = ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.login_form_edittext_focused
                )
            } else {
                check_password.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.login_form_edittext)
            }
        }

        save_btn.setSafeOnClickListener {
            if (loadingLayout.visibility != View.VISIBLE) {
                if (original_password.text.toString().trim() != "") {
                    if (new_password.text.toString().length < 8) {
                        new_password.error = "비밀번호는 최소 8자여야 합니다"
                    } else if (new_password.text.toString() != check_password.text.toString()) {
                        check_password.error = "비밀번호가 일치하지 않습니다"
                    } else if (original_password.text.toString() == new_password.text.toString()) {
                        new_password.error = "비밀번호가 상통합니다"
                    } else try {
                        loadingLayout.visibility = View.VISIBLE
                        animator1.start(); animator2.start(); animator3.start(); animator4.start()
                        sendPostRequest(original_password.text.toString().trim(), new_password.text.toString().trim(), username, animator1, animator2, animator3, animator4)
                    } catch (e: Exception) {
                        // Log.d("Exception:", e.toString())
                    }
                }
            }
        }
    }

    fun sendPostRequest(originalPassword:String, newPassword:String, username:String, animator1: ObjectAnimator, animator2: ObjectAnimator, animator3: ObjectAnimator, animator4: ObjectAnimator) {
        var reqParam = URLEncoder.encode("originalPassword", "UTF-8") + "=" + URLEncoder.encode(originalPassword.sha256(), "UTF-8")
        reqParam += "&" + URLEncoder.encode("newPassword", "UTF-8") + "=" + URLEncoder.encode(newPassword.sha256(), "UTF-8")
        reqParam += "&" + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8")
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
                loadingLayout.visibility = View.GONE
                animator1.pause(); animator2.pause(); animator3.pause(); animator4.pause()
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
    private fun String.sha256(): String {
        val md = MessageDigest.getInstance("SHA-256")
        return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
    }
    fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }
}