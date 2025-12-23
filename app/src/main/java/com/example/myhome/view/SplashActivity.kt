package com.example.myhome.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.SemanticsProperties.Text
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myhome.R
import com.example.myhome.domain.response.Result
import com.example.myhome.viewmodel.SplashViewmodel

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewmodel : SplashViewmodel by viewModels()
        setContent {
            SplashScreen(viewmodel,{
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

}
@Composable
fun SplashScreen(
    viewmodel: SplashViewmodel,
    onFailToAuth: ()-> Unit,
    onMainActivity: ()-> Unit,
) {
    var isError by remember{
        mutableStateOf(false)
    }

    val isSwitchToMainActivity = viewmodel.response.collectAsState(Result.Nothing)
    when(isSwitchToMainActivity.value){
        is Result.Nothing -> {
        }
        is Result.Loading -> {

        }
        is Result.Response<*> -> {
            onMainActivity()
        }
        is Result.Error -> {
            if((isSwitchToMainActivity.value as Result.Error).message == "No user found"){
                onFailToAuth()
            }else   isError = true

        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A1F4D))
    ) {
        if(isError){
            Icon(
                painter = painterResource(id = R.drawable.error),
                contentDescription = "Splash",
                modifier = Modifier.size(100.dp).align(Alignment.Center),
                tint = Color.White,
            )
        }
        else{
            Column(
                modifier = Modifier
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.smart_home),
                    contentDescription = "Splash",
                    modifier = Modifier.size(100.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Smart Connect",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold),

                    )
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Control your home easier",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }

    }

}
