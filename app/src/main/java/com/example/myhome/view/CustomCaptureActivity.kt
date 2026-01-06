package com.example.myhome.view

import android.view.View
import com.example.myhome.R
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.DecoratedBarcodeView

class CustomCaptureActivity : CaptureActivity() {

    override fun initializeContent(): DecoratedBarcodeView {
        setContentView(R.layout.activity_custom_capture)
        return findViewById<View?>(R.id.zxing_barcode_scanner) as DecoratedBarcodeView
    }
}