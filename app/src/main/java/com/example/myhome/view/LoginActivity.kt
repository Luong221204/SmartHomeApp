package com.example.myhome.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myhome.R
import com.example.myhome.domain.response.Result
import com.example.myhome.viewmodel.LoginViewmodel
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myhome.compose.ForgotPasswordScreen
import com.example.myhome.compose.ResetPasswordScreen
import com.example.myhome.compose.RoundedInput
import com.example.myhome.domain.User
import com.example.myhome.network.ApiConnect
import com.example.myhome.ui.theme.AppTheme
import kotlinx.coroutines.flow.SharedFlow
import retrofit2.Response

class LoginActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewmodel : LoginViewmodel by viewModels()
        setContent {
            AppTheme{
                Scaffold() {
                        paddingValues ->
                    val r = rememberNavController()
                    val coroutine = rememberCoroutineScope()
                    NavHost(
                        navController =  r,
                        startDestination = "login"
                    ) {
                        composable("login?email={email}&password={password}"){
                            val email = it.arguments?.getString("email")
                            val password = it.arguments?.getString("password")
                            LoginScreen(email,password,
                                modifier = Modifier.fillMaxSize(),viewmodel.login,{ name, email, password->
                                    viewmodel.login(name,email,password)
                                },{
                                    val intent = Intent(it, RegisterActivity::class.java)
                                    startActivity(intent)
                                },{
                                    r.navigate("forgot")
                                },{
                                    val intent = Intent(it, MainActivity::class.java)
                                    startActivity(intent)
                                })
                        }
                        composable("forgot"){
                            ForgotPasswordScreen(modifier = Modifier.padding(paddingValues).fillMaxSize(), viewmodel.forgot,onBack = {
                                r.navigate("login")
                            },{viewmodel.forgotPassword(it)}){
                                Log.d("DUCLUONG","tu compose $it")

                                r.navigate("reset/$it")
                            }
                        }
                        composable("reset/{email}"){
                            val email = it.arguments?.getString("email")
                            ResetPasswordScreen(email!!,modifier = Modifier.padding(paddingValues).fillMaxSize(), viewmodel.reset,onBack = {
                                r.popBackStack()
                            },{email,opt,password->
                                viewmodel.resetPassword(email,opt,password)
                            }){
                                    i,i2->
                                r.navigate("login")
                            }
                        }
                    }

                }
            }

        }
    }
}


@Composable
fun LoginScreen(
    e: String?,
    p: String?,
    modifier: Modifier,
    state: SharedFlow<Result>,
    onLoginClick: (fullName: String, email: String, password: String) -> Unit,
    onRegisterClick: (Context) -> Unit = { },
    onForgotClick: () -> Unit = { },
    onSuccess: ((Context) -> Unit),
) {
    val fullName = remember { mutableStateOf("") }
    var email = remember { mutableStateOf(e ?: "") }
    var password = remember { mutableStateOf(p ?: "") }
    val confirmPass = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }
    val confirmPasswordVisible = remember { mutableStateOf(false) }
    val loginState = state.collectAsState(initial = Result.Nothing)
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    LaunchedEffect(loginState.value) {
        when(loginState.value){
            is Result.Error -> {
                isLoading = false
                Toast.makeText(context,(loginState.value as Result.Error).message.toString(),Toast.LENGTH_SHORT).show()
            }
            is Result.Response<*> -> {
                isLoading = false
                onSuccess(context)
            }
            is Result.Loading -> {
                isLoading=true
            }

            else -> {}
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.color.backgroundAppColor)
    ) {

        Image(
            painter = painterResource(R.drawable.house_background),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(AppTheme.dimen.heightLargeImage),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal =AppTheme.padding.largeHorizontalPadding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(AppTheme.spacer.fromTop))

            Text(
                "Login",
                style = AppTheme.typography.infoLargeTitle,
                color = Color.White
            )

            Spacer(Modifier.height(AppTheme.spacer.loginSpacer))

            RoundedInput(
                value = email,
                label = "Your mail",
                icon = R.drawable.ic_email
            )
            Spacer(Modifier.height(AppTheme.spacer.heightDash))

            RoundedInput(
                value = password,
                label = "Password",
                icon = R.drawable.ic_lock,
                isPassword = true,
                passwordVisible = passwordVisible
            )
            Spacer(Modifier.height(AppTheme.spacer.heightDash))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
                Text("Quên mật khẩu",
                    color = Color.Magenta,
                    style = AppTheme.typography.additionTitle,
                    modifier = Modifier.clickable { onForgotClick() },
                    )
            }

            Spacer(Modifier.height(AppTheme.spacer.heightDash))

            Button(
                onClick = {
                    onLoginClick(fullName.value, email.value, password.value)
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppTheme.dimen.heightLargeButton),
                shape = AppTheme.corner.buttonCorner,
                colors = ButtonDefaults.buttonColors(
                    containerColor =if(isLoading) AppTheme.color.unEnableButton else  AppTheme.color.enableButton
                )
            ) {
                if(isLoading){
                    CircularProgressIndicator(
                        modifier = Modifier.size(AppTheme.dimen.iconLargeSize),
                        color = AppTheme.color.circularButton,
                        strokeWidth = AppTheme.dimen.strokeWidth
                    )
                }
                else
                Text("Login", color =  AppTheme.color.textButtonColor,style = AppTheme.typography.largeButtonTitle)
            }

            Spacer(Modifier.height(AppTheme.spacer.heightDash))
            Row {
                Text("Don't have a account?", color = AppTheme.color.policyColor)
                Spacer(Modifier.width(AppTheme.spacer.multiTextGap))
                Text(
                    "Sign Up",
                    color = AppTheme.color.additionColor,
                    style = AppTheme.typography.additionTitle,
                    modifier = Modifier.clickable { onRegisterClick(context)}
                )
            }
        }
    }
}