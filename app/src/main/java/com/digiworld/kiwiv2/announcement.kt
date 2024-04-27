package com.digiworld.kiwiv2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class announcement : AppCompatActivity() {

    private lateinit var announcement_id: String
    private lateinit var announcement_title: TextView
    private lateinit var announcement_body: TextView
    private lateinit var button: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_announcement)

        announcement_title = findViewById(R.id.announcement_title)
        announcement_body = findViewById(R.id.announcement_body)
        button = findViewById(R.id.button)

        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey("announcement_id")) {
                announcement_id = extras.getString("announcement_id").toString()
            }
            if (extras.containsKey("announcement_title")) {
                announcement_title.text = extras.getString("announcement_title")
            }
            if (extras.containsKey("announcement_body")) {
                announcement_body.text = extras.getString("announcement_body")
            }
        }

        button.setOnClickListener {
            finish()
        }
    }
}