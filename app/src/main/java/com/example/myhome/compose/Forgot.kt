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
import com.example.myhome.domain.response.NetworkResult
import com.example.myhome.domain.response.Result
import com.example.myhome.network.ApiConnect
import com.example.myhome.ui.theme.AppTheme
import com.example.myhome.view.ConfirmDialog
import com.example.myhome.viewmodel.ForgotPasswordUiState
import com.example.myhome.viewmodel.LoginViewmodel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    modifier: Modifier,
    viewmodel: LoginViewmodel,
    onBack: () -> Unit,
    onSuccess:(String)->Unit
) {
    val keyBoard = LocalSoftwareKeyboardController.current
    val forgotState = viewmodel.uiState.collectAsState()
    LaunchedEffect(forgotState.value.isSuccess) {
        if (forgotState.value.isSuccess) {
            onSuccess(forgotState.value.email)
        }
    }
    Box(
        modifier = modifier
            .background(Color(0xFFF5F7FB))
            .padding(horizontal = 16.dp)
    ) {
        if (forgotState.value.showDialog) {
            InfoDialog(
                modifier= Modifier.align(Alignment.Center),
                isSuccess = forgotState.value.isSuccess,
                forgotState.value.status,
                forgotState.value.error?:"",
                onDismiss = { viewmodel.dismissDialog() }
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
                    value = forgotState.value.email,
                    onValueChange = {
                        viewmodel.onEmailChange(it)
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

                    isError = forgotState.value.isEmailError != null,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { viewmodel.sendOtp()
                            keyBoard?.hide()
                        }
                    ),
                    shape = AppTheme.corner.buttonCorner
                )

            }


            if (forgotState.value.isEmailError != null) {
                Text(
                    text = forgotState.value.isEmailError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Spacer(modifier = Modifier.height(AppTheme.spacer.heightDash))

            // Submit button (rounded, blue)
            Button(
                onClick = { viewmodel.sendOtp()
                          keyBoard?.hide()},
                enabled = !forgotState.value.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppTheme.dimen.heightLargeButton),
                shape = AppTheme.corner.buttonCorner,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if(forgotState.value.isLoading) AppTheme.color.unEnableButton else AppTheme.color.largeButton, // blue like screenshot
                    contentColor = AppTheme.color.textButtonColor
                )
            ) {
                if (forgotState.value.isLoading) {
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

