package com.example.myhome.compose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import com.example.myhome.R

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.myhome.domain.User
import com.example.myhome.domain.response.Result
import com.example.myhome.network.ApiConnect
import com.example.myhome.ui.theme.AppTheme
import com.example.myhome.view.ConfirmDialog
import kotlinx.coroutines.flow.SharedFlow
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    modifier: Modifier,
    state: SharedFlow<Result>,
    onBack: () -> Unit,
    onSubmit: (String) -> Unit,
    onSuccess:(String)->Unit
) {
    val keyBoard = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var isShowDialog by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    val forgotState = state.collectAsState(Result.Nothing)
    when(forgotState.value){
        is Result.Error -> {
            isLoading = false
            isShowDialog = true
            status = false
            message = (forgotState.value as Result.Error).message?:""
        }
        is Result.Response<*> -> {
            isLoading = false
            onSuccess(email)
        }
        is Result.Loading -> {
            isLoading = true
        }

        else -> {}
    }
    // Simple email validator
    fun validateAndSubmit() {
        emailError = when {
            email.isBlank() -> "Please enter your email"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email"
            else -> null
        }
        if (emailError == null) {
            loading = true
            // simulate network call or call real API
            onSubmit(email)
            // keep loading handling to your actual network callback
            loading = false
            focusManager.clearFocus()
        }
    }

    Box(
        modifier = modifier
            .background(Color(0xFFF5F7FB))
            .padding(horizontal = 16.dp)
    ) {
        if (isShowDialog) {
            InfoDialog(
                modifier= Modifier.align(Alignment.Center),
                isSuccess = isSuccess,
                status,
                message,
                onDismiss = { isShowDialog = false }
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()

        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        painter = painterResource(R.drawable.back),
                        contentDescription = "Back",
                        modifier = Modifier.size(AppTheme.dimen.iconSmallSize)
                    )
                }

                Spacer(modifier = Modifier.width(AppTheme.spacer.smallGap))

                Text(
                    text = "Forgot Password",
                    style = AppTheme.typography.introSectionTitle,
                )
            }

            Spacer(modifier = Modifier.height(AppTheme.spacer.heightDash))

            // Subtitle / hint (can be removed if not in screenshot)
            Text(
                text = "Enter your email",
                style = AppTheme.typography.placeHolder,
                color = AppTheme.color.policyColor,
            )
            Spacer(modifier = Modifier.height(AppTheme.spacer.smallGap))

            // Email input box style similar to screenshot
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        if (emailError != null) emailError = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(AppTheme.dimen.heightLargeButton),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_email),
                            contentDescription = "Email",
                            modifier = Modifier.size(AppTheme.dimen.iconLargeSize)

                        )
                    },
                    placeholder = {
                        Text(text = "Enter your email",
                           style = AppTheme.typography.placeHolder,
                            color = AppTheme.color.policyColor)
                    },
                    isError = emailError != null,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { validateAndSubmit()
                            keyBoard?.hide()
                        }
                    ),
                    shape = AppTheme.corner.buttonCorner
                )

            }

            if (emailError != null) {
                Text(
                    text = emailError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Spacer(modifier = Modifier.height(AppTheme.spacer.heightDash))

            // Submit button (rounded, blue)
            Button(
                onClick = { validateAndSubmit()
                          keyBoard?.hide()},
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppTheme.dimen.heightLargeButton),
                shape = AppTheme.corner.buttonCorner,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if(isLoading) AppTheme.color.unEnableButton else AppTheme.color.largeButton, // blue like screenshot
                    contentColor = AppTheme.color.textButtonColor
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(AppTheme.dimen.iconLargeSize),
                        strokeWidth = AppTheme.dimen.strokeWidth,
                        color = AppTheme.color.textButtonColor
                    )
                }else
                Text(text = "Submit", style = AppTheme.typography.largeButtonTitle,color=  AppTheme.color.textButtonColor)
            }

        }
    }
}

