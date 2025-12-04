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
import com.example.myhome.viewmodel.GasViewmodel

class GasActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewmodel: GasViewmodel by viewModels()
        setContent {
            Scaffold() { contentPadding ->
                SensorScreen(
                    Modifier
                        .padding(contentPadding)
                        .fillMaxSize(),
                    "Gas sensor",
                    "Biểu đồ khí gas",
                    4095,
                    1000,
                    viewmodel.gasSensor,
                    viewmodel.sendStatus,
                    viewmodel.value,
                    viewmodel.list,
                    viewmodel.status,
                    viewmodel.info,
                    {
                        viewmodel.updateGsStatus(it)
                    },
                    {
                        viewmodel.updateGsLevel(it)
                    }
                ){
                    finish()
                }
            }
        }
    }
}

