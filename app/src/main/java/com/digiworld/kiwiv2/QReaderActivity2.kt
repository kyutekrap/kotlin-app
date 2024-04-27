package com.digiworld.kiwiv2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DecoratedBarcodeView.TorchListener

class QReaderActivity2 : AppCompatActivity(), TorchListener {
    private var manager: CaptureManager? = null
    private var isFlashOn = false // 플래시가 켜져 있는지
    private var btFlash: Button? = null
    private var barcodeView: DecoratedBarcodeView? = null
    private lateinit var cancel: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qr_camera_for_multiple_transactions)
        barcodeView = findViewById(R.id.frameLayout)
        manager = CaptureManager(this, barcodeView)
        manager!!.initializeFromIntent(intent, savedInstanceState)
        manager!!.decode()

        cancel = findViewById(R.id.cancel)

        cancel.setOnClickListener {
            this.finish()
        }
    }

    override fun onTorchOn() {
        btFlash?.setText("플래시끄기")
        isFlashOn = true
    }

    override fun onTorchOff() {
        btFlash?.setText("플래시켜기")
        isFlashOn = false
    }

    override fun onResume() {
        super.onResume()
        manager!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        manager!!.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        manager!!.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        manager!!.onSaveInstanceState(outState)
    }
}