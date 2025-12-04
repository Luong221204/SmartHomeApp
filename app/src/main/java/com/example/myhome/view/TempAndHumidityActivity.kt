package com.example.myhome.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myhome.R
import com.example.myhome.compose.ChartScreen
import com.example.myhome.domain.response.Result
import com.example.myhome.viewmodel.TaHViewmodel

class TempAndHumidityActivity : BaseActivity() {
    val viewmodel : TaHViewmodel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Scaffold(modifier = Modifier.fillMaxSize()) {
                TaHScreen(viewmodel,Modifier.padding(it),{
                    finish()
                })
            }
        }
    }
}

@Composable
fun TaHScreen(viewmodel: TaHViewmodel ,modifier: Modifier,back:()->Unit) {
    val status = viewmodel.status.collectAsState()
    var isLoading by remember { mutableStateOf(false) }
    when (status.value) {
        is Result.Loading -> { isLoading = true }
        is Result.Response<*> -> { isLoading = false }
        is Result.Error -> {isLoading = false}
        is Result.Nothing -> {isLoading = false}
    }
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(color = Color.White)
                    .padding(start = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.back),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            back()
                        }
                )
                Spacer(Modifier.width(20.dp))
                Text(
                    text = "Temperature and Humidity",
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                )
            }
            Spacer(Modifier.height(24.dp))

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                Text(
                    text = "Biểu đồ nhiệt độ",
                    modifier= Modifier.padding(start = 16.dp),
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
                )
                ChartScreen(list = viewmodel.list_temp.value)
                Text(
                    text = "Biểu đồ độ ẩm",
                    modifier= Modifier.padding(start = 16.dp),
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
                )
                ChartScreen(list = viewmodel.list_humid.value)
            }
        }
    }
}