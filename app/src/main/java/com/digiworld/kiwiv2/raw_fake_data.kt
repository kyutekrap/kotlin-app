package com.digiworld.kiwiv2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView

class raw_fake_data : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_raw_fake_data)

        webView = findViewById(R.id.webView)
        textView = findViewById(R.id.textView)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey("productName")) {
                textView.text = extras.getString("productName").toString()
                webView.webViewClient = WebViewClient()
                webView.loadUrl("https://dongkye.tech/freewebhosting/productpage.php?fundId=" + extras.getString("productName").toString())
            }
        }
    }
}