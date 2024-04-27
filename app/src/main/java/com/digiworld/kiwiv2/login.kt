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
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
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

class login : AppCompatActivity() {

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var loginbtn: TextView
    private lateinit var _signin: TextView
    private lateinit var incorrect: TextView
    private lateinit var loadingLayout: LinearLayout
    private lateinit var loading1: View
    private lateinit var loading2: View
    private lateinit var loading3: View
    private lateinit var loading4: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        loginbtn = findViewById(R.id.loginbtn)
        _signin = findViewById(R.id.signin)
        incorrect = findViewById(R.id.incorrect)
        loadingLayout = findViewById(R.id.loadinglayout)
        loading1 = findViewById(R.id.loading1)
        loading2 = findViewById(R.id.loading2)
        loading3 = findViewById(R.id.loading3)
        loading4 = findViewById(R.id.loading4)

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

        _signin.setOnClickListener {
            startActivity(Intent(this, signin::class.java))
        }

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        username.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                username.background = ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.login_form_edittext_focused
                )
            } else {
                username.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.login_form_edittext)
            }
        }
        password.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                password.background = ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.login_form_edittext_focused
                )
            } else {
                password.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.login_form_edittext)
            }
        }

        loginbtn.setSafeOnClickListener {
            if (loadingLayout.visibility != View.VISIBLE) {
                if (username.text.toString().trim() != "") {
                    if (password.text.toString().length < 8) {
                        password.error = "비밀번호는 최소 8자여야 합니다"
                    } else {
                        try {
                            loadingLayout.visibility = View.VISIBLE
                            animator1.start(); animator2.start(); animator3.start(); animator4.start()
                            sendPostRequest(
                                username.text.toString().trim().sha256(),
                                password.text.toString().trim().sha256(),
                                animator1,
                                animator2,
                                animator3,
                                animator4
                            )
                        } catch (e: Exception) {
                            // Log.d("Exception:", e.toString())
                        }
                    }
                }
            }
        }

    }

    fun sendPostRequest(userName:String, password:String, animator1: ObjectAnimator, animator2: ObjectAnimator, animator3: ObjectAnimator, animator4: ObjectAnimator) {
        var reqParam = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(userName, "UTF-8")
        reqParam += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8")
        val mURL = URL("https://www.dongkye.tech/A5/login.php")
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
                    val sharedPreference =  getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
                    sharedPreference.edit().putString("username", userName).apply()
                    val intent = Intent(applicationContext, MainParent::class.java)
                    startActivity(intent)
                } else {
                    loadingLayout.visibility = View.GONE
                    if (jsonObj.getString("success") == "wrong") {
                        incorrect.visibility = View.VISIBLE
                    } else if (jsonObj.getString("success") == "false") {
                        incorrect.text = "네트워크 연결이 불안정합니다"
                        incorrect.visibility = View.VISIBLE
                    }
                    animator1.pause(); animator2.pause(); animator3.pause(); animator4.pause()
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