package com.example.myhome.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.myhome.compose.SensorScreen
import com.example.myhome.viewmodel.FlameViewmodel
import com.example.myhome.viewmodel.GasViewmodel
import kotlin.getValue

class FlameActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewmodel: FlameViewmodel by viewModels()
        viewmodel.status
        setContent {
            Scaffold() { contentPadding ->
                SensorScreen(
                    Modifier
                        .padding(contentPadding)
                        .fillMaxSize(),
                    "Flame sensor",
                    "Biểu đồ khả năng cháy",
                    4095,
                    1000,
                    viewmodel.flameSensor,
                    viewmodel.sendStatus,
                    viewmodel.value,
                    viewmodel.list,
                    viewmodel.status,
                    viewmodel.info,
                    {
                        viewmodel.updateFsStatus(it)
                    },
                    {
                        viewmodel.updateFsLevel(it)
                    }
                ){
                    finish()
                }
            }
        }
    }
}