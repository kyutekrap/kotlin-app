package com.digiworld.kiwiv2

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.encoder.QRCode

class pay : Fragment() {

    private lateinit var useCamera: TextView
    private lateinit var qrCode: ImageView
    private lateinit var frameLayout: FrameLayout
    private lateinit var myQR: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.pay_main, container, false)

        useCamera = view.findViewById(R.id.useCamera)
        qrCode = view.findViewById(R.id.qrCode)
        frameLayout = view.findViewById(R.id.frameLayout)
        myQR = view.findViewById(R.id.myQR)

        val content = (activity as MainParent).displayName+"::"+(activity as MainParent).receiver_id
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }
        qrCode.setImageBitmap(bitmap)

        frameLayout.visibility = View.GONE

        useCamera.setOnClickListener {
            if (useCamera.text == "스캔하기") {
                checkCameraPermissions(requireContext())
                if (checkCameraHardware(requireContext())) {
                    initQRcodeScanner()
                } else {
                    Toast.makeText(requireContext(), "카메라 권한이 없습니다", Toast.LENGTH_LONG).show()
                }
            }
        }

        return view
    }

    private fun initQRcodeScanner() {
        val intentIntegrator = IntentIntegrator.forSupportFragment(this)
        intentIntegrator.setBeepEnabled(false)
        intentIntegrator.captureActivity = QReaderActivity::class.java
        intentIntegrator.initiateScan()
    }
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        val intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (intentResult != null) {
            if (intentResult.contents == null) {
                Toast.makeText(requireContext(), "취소되었습니다", Toast.LENGTH_LONG).show()
            } else {
                if (intentResult.contents.contains("::")) {
                    val displayName = intentResult.contents.split("::")[0]
                    val receiverId = intentResult.contents.split("::")[1]
                    val intent = Intent(requireContext(), pay_with_custom_keypad::class.java)
                    intent.putExtra("recipient", displayName)
                    intent.putExtra("recipient_address", receiverId)
                    intent.putExtra("baseCurrency", (activity as MainParent).baseCurrency)
                    startActivity(intent)
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun checkCameraHardware(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }
    private fun checkCameraPermissions(context: Context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                (context as Activity?)!!, arrayOf(Manifest.permission.CAMERA),
                100
            )
        }
    }
}

