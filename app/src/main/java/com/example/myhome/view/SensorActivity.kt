package com.example.myhome.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.compose.AsyncImage
import com.example.myhome.R
import com.example.myhome.compose.skeleton.FullCardSkeleton
import com.example.myhome.compose.skeleton.LineChartSkeleton
import com.example.myhome.compose.templates.CustomTopAppBar
import com.example.myhome.compose.templates.Sensor
import com.example.myhome.compose.templates.SensorScreen
import com.example.myhome.graph.MainScreen
import com.example.myhome.ui.theme.AppTheme
import com.example.myhome.viewmodel.SensorViewmodel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SensorActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewmodel : SensorViewmodel by viewModels()
        viewmodel.getSensorById("fUBZH5TAKi5Y8hTiUhfb")
        val intent = getIntent()
        val sensorId = intent.getStringExtra("sensorId")
        setContent {
            AppTheme{
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CustomTopAppBar()
                    }
                ) {
                    innerPadding->
                    Column(
                        modifier = Modifier.fillMaxSize().padding(innerPadding)
                    ){
                        Spacer(modifier = Modifier.height(16.dp))
                        val sensorUiState = viewmodel.sensorById.collectAsState()
                        if(sensorUiState.value.isLoading){
                            SensorScreenSkeleton(modifier = Modifier.fillMaxSize(),isLoading = sensorUiState.value.isLoading)
                        }
                        else{
                            SensorScreen(
                                sensor = sensorUiState.value.sensor?: Sensor(),
                                modifier = Modifier.fillMaxSize(),
                                switchState = sensorUiState.value.switchState,
                            ){
                                viewmodel.setSwitchState(it)
                            }
                        }
                    }

                }
            }

        }
    }
}

@Composable
fun SensorScreenSkeleton(
    isLoading: Boolean,
    modifier: Modifier = Modifier
){
    if(isLoading){
        Column(
            modifier = modifier
        ) {
            FullCardSkeleton(true) { }
            Spacer(modifier = Modifier.height(16.dp))
            LineChartSkeleton(true){}
        }
    }
}


