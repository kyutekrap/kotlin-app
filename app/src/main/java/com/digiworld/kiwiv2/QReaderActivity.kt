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

class QReaderActivity : AppCompatActivity(), TorchListener {

    private var manager: CaptureManager? = null
    private var isFlashOn = false // 플래시가 켜져 있는지
    private var btFlash: Button? = null
    private var barcodeView: DecoratedBarcodeView? = null
    private lateinit var useCamera: TextView
    private lateinit var qrCode: ImageView
    private lateinit var myQR: TextView
    private lateinit var description: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pay_main)
        barcodeView = findViewById(R.id.frameLayout)
        manager = CaptureManager(this, barcodeView)
        manager!!.initializeFromIntent(intent, savedInstanceState)
        manager!!.decode()

        useCamera = findViewById(R.id.useCamera)
        myQR = findViewById(R.id.myQR)
        qrCode = findViewById(R.id.qrCode)
        description = findViewById(R.id.description)

        qrCode.visibility = View.GONE

        useCamera.text = "내 QR코드 보기"
        myQR.text = "QR코드 찾기"
        description.text = "타 이용자의 코드를 스캔해서\n코인을 송금할 수 있습니다"

        useCamera.setOnClickListener {
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