package com.example.myhome.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import com.example.myhome.R
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myhome.compose.RoundedInput
import com.example.myhome.domain.User
import com.example.myhome.domain.response.Result
import com.example.myhome.domain.response.ResultFromHandle
import com.example.myhome.ui.theme.AppTheme
import com.example.myhome.viewmodel.RegisterViewmodel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.withContext
import retrofit2.Response

class RegisterActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewmodel : RegisterViewmodel by viewModels()
        setContent {
            AppTheme{
                Scaffold() {
                        paddingValues ->
                    RegisterScreen(modifier = Modifier.fillMaxSize(),viewmodel.register,{
                        finish()
                    }){
                            fullName, email, password ,confirm ->
                        viewmodel.register(fullName,email,password,confirm)
                    }
                }
            }

        }
    }
}

@Composable
fun RegisterScreen(
    modifier: Modifier,
    state:SharedFlow<Result>,
    onLoginClick: () -> Unit = {},
    onRegisterClick: (String, String, String,String) -> Unit
) {
    val fullName = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPass = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }
    val confirmPasswordVisible = remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val registerState = state.collectAsState(Result.Nothing)
    LaunchedEffect(registerState.value) {
        when(registerState.value){
            is Result.Error -> {
                isLoading = false
                Toast.makeText(context, (registerState.value as Result.Error).message,Toast.LENGTH_SHORT).show()
            }
            is Result.Response<*> -> {
                isLoading = false
                onLoginClick()
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
                .padding(horizontal = AppTheme.padding.largeHorizontalPadding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(AppTheme.spacer.fromTop))

            Text(
                "Register",
                style = AppTheme.typography.infoLargeTitle,
                color = AppTheme.color.infoLargeColor
            )

            Text(
                "Create a new account",
                style = AppTheme.typography.infoAdditionTitle,
                color = AppTheme.color.infoAdditionColor
            )

            Spacer(Modifier.height(AppTheme.spacer.largeSpacer))


            Spacer(Modifier.height(AppTheme.spacer.heightDash))
            RoundedInput(
                value = fullName,
                label = "Fullname",
                icon = R.drawable.ic_user
            )

            Spacer(Modifier.height(AppTheme.spacer.heightDash))
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
            RoundedInput(
                value = confirmPass,
                label = "Confirm Password",
                icon = R.drawable.ic_lock,
                isPassword = true,
                passwordVisible = confirmPasswordVisible
            )

            Spacer(Modifier.height(AppTheme.spacer.heightDash))

            Text(
                text = "By creating an account, you agree to our Terms & Conditions and Privacy Policy",
                style = AppTheme.typography.policyTitle,
                color =  AppTheme.color.policyColor,
            )

            Spacer(Modifier.height(AppTheme.spacer.heightDash))

            Button(
                onClick = {
                    onRegisterClick(fullName.value, email.value, password.value,confirmPass.value)
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppTheme.dimen.heightLargeButton),
                shape = AppTheme.corner.buttonCorner,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if(isLoading) AppTheme.color.unEnableButton else  AppTheme.color.enableButton
                )
            ) {
                if(isLoading)
                    CircularProgressIndicator(
                        color = AppTheme.color.circularButton ,
                        strokeWidth = AppTheme.dimen.strokeWidth,
                        modifier = Modifier.size(AppTheme.dimen.iconLargeSize)
                    )
                else
                Text("Register", color = AppTheme.color.textButtonColor,style = AppTheme.typography.largeButtonTitle)
            }

            Spacer(Modifier.height(AppTheme.spacer.heightDash))

            Row {
                Text("Already have an account?", color = AppTheme.color.policyColor)
                Spacer(Modifier.width(AppTheme.spacer.multiTextGap))
                Text(
                    "Log In",
                    color = AppTheme.color.additionColor,
                    style = AppTheme.typography.additionTitle,
                    modifier = Modifier.clickable { onLoginClick() }
                )
            }
        }
    }
}
