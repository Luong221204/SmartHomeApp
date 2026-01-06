package com.example.myhome.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myhome.R
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class ScanActivity : BaseActivity() {
    companion object {
        private const val CAMERA_REQUEST = 1001
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkCameraPermission()

    }
    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startQrScan()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startQrScan() {
        val scan = ScanOptions()
        scan.setPrompt("Quét mã qr")
        scan.setBeepEnabled(true)
        scan.setOrientationLocked(false)
        scan.setBarcodeImageEnabled(true)
        scan.setCaptureActivity(CaptureActivity::class.java)
        scanLauncher.launch(scan)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            startQrScan()
            Toast.makeText(this,getString(R.string.success_statement),Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, getString(R.string.warning_statement),Toast.LENGTH_SHORT).show()

        }
    }

    private val scanLauncher = registerForActivityResult(ScanContract()){
        result->
        if(result.contents != null){
            handleQrResult(result.contents)
        }else{
            Toast.makeText(this, "Huỷ quét", Toast.LENGTH_SHORT).show()
            finish()
        }
    }


    private fun handleQrResult(qrText: String) {
        // Ví dụ: QR chỉ chứa houseId
        val houseId = qrText

        Toast.makeText(this, "House ID: $houseId", Toast.LENGTH_LONG).show()

        // TODO:
        // - Gửi houseId + FCM token lên server
        // - Hoặc lưu SharedPreferences
    }
}