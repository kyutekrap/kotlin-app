package com.digiworld.kiwiv2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView


class deposit_or_withdraw_on_service : AppCompatActivity() {

    private lateinit var nextButton: TextView
    private lateinit var serviceName: TextView
    private lateinit var withdraw_or_deposit: TextView
    private lateinit var total: TextView
    private lateinit var one: TextView
    private lateinit var two: TextView
    private lateinit var three: TextView
    private lateinit var four: TextView
    private lateinit var five: TextView
    private lateinit var six: TextView
    private lateinit var seven: TextView
    private lateinit var eight: TextView
    private lateinit var nine: TextView
    private lateinit var zero: TextView
    private lateinit var dot: TextView
    private lateinit var del: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.deposit_or_withdraw_on_service)

        nextButton = findViewById(R.id.nextButton)
        serviceName = findViewById(R.id.serviceName)
        withdraw_or_deposit = findViewById(R.id.deposit_or_withdraw)
        total = findViewById(R.id.total)
        one = findViewById(R.id.one)
        two = findViewById(R.id.two)
        three = findViewById(R.id.three)
        four = findViewById(R.id.four)
        five = findViewById(R.id.five)
        six = findViewById(R.id.six)
        seven = findViewById(R.id.seven)
        eight = findViewById(R.id.eight)
        nine = findViewById(R.id.nine)
        zero = findViewById(R.id.zero)
        dot = findViewById(R.id.dot)
        del = findViewById(R.id.del)

        val activity:String = intent.getStringExtra("activitiy").toString()
        val service_name:String = intent.getStringExtra("serviceName").toString()

        withdraw_or_deposit.setText(activity)
        serviceName.setText(service_name)

        nextButton.setOnClickListener {
            var thisTotal: String = total.text.toString()
        }

        one.setOnClickListener {
            var thisTotal: String = total.text.toString()
            thisTotal+="1"
            total.setText(thisTotal)
        }
        two.setOnClickListener {
            var thisTotal: String = total.text.toString()
            thisTotal+="2"
            total.setText(thisTotal)
        }
        three.setOnClickListener {
            var thisTotal: String = total.text.toString()
            thisTotal+="3"
            total.setText(thisTotal)
        }
        four.setOnClickListener {
            var thisTotal: String = total.text.toString()
            thisTotal+="4"
            total.setText(thisTotal)
        }
        five.setOnClickListener {
            var thisTotal: String = total.text.toString()
            thisTotal+="5"
            total.setText(thisTotal)
        }
        six.setOnClickListener {
            var thisTotal: String = total.text.toString()
            thisTotal+="6"
            total.setText(thisTotal)
        }
        seven.setOnClickListener {
            var thisTotal: String = total.text.toString()
            thisTotal+="7"
            total.setText(thisTotal)
        }
        eight.setOnClickListener {
            var thisTotal: String = total.text.toString()
            thisTotal+="8"
            total.setText(thisTotal)
        }
        nine.setOnClickListener {
            var thisTotal: String = total.text.toString()
            thisTotal+="9"
            total.setText(thisTotal)
        }
        zero.setOnClickListener {
            var thisTotal: Float = total.text.toString().toFloat()
            if (!thisTotal.equals(0)) {
                total.setText(total.text.toString()+"0")
            }
        }
        dot.setOnClickListener {
            var thisTotal: String = total.text.toString()
            if (!thisTotal.contains(".", ignoreCase = true)) {
                thisTotal+="."
                total.setText(thisTotal)
            }
        }
        del.setOnClickListener {
            var thisTotal: String = total.text.toString()
            thisTotal = thisTotal.dropLast(1)
            total.setText(thisTotal)
        }
    }
}