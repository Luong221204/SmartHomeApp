package com.example.myhome.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myhome.R
import com.example.myhome.compose.SensorScreen
import com.example.myhome.ui.theme.AppTheme
import com.example.myhome.viewmodel.GasViewmodel
import com.example.myhome.viewmodel.RainViewmodel
import kotlin.getValue

class RainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewmodel: RainViewmodel by viewModels()
        setContent {
            AppTheme{
                Scaffold() { contentPadding ->
                    SensorScreen(
                        Modifier
                            .padding(contentPadding)
                            .fillMaxSize(),
                        "Rain sensor",
                        "Biểu đồ khả năng mưa",
                        4095,
                        1000,
                        viewmodel.rainSensor,
                        viewmodel.sendStatus,
                        viewmodel.value,
                        viewmodel.list,
                        viewmodel.status,
                        viewmodel.info,
                        {
                            viewmodel.updateRsStatus(it)
                        },
                        {
                            viewmodel.updateRsLevel(it)
                        }
                    ){
                        finish()
                    }
                }
            }

        }
    }
}