package com.example.myhome.compose

import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myhome.R
import com.example.myhome.domain.User
import com.example.myhome.domain.response.NetworkResult
import com.example.myhome.domain.response.Result
import com.example.myhome.ui.theme.AppTheme
import com.example.myhome.viewmodel.LoginViewmodel
import kotlinx.coroutines.flow.SharedFlow
import retrofit2.Response


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(
    email: String,
    modifier: Modifier,
    viewmodel: LoginViewmodel,
    onBack: () -> Unit,
    onSuccess:(String,String)->Unit
) {
    val keyBoard = LocalSoftwareKeyboardController.current
    val resetState = viewmodel.uiResetState.collectAsState()
    Box(
        modifier = modifier
            .background(AppTheme.color.backgroundAppColor)
            .padding(horizontal = AppTheme.padding.smallHorizontalPadding)
    ) {

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
                    text = "Reset Password",
                    style = AppTheme.typography.introSectionTitle,
                )
            }

            Spacer(modifier = Modifier.height(AppTheme.spacer.heightDash))

            Text(
                text = "Enter OTP",
                style  = AppTheme.typography.placeHolder,
                color = AppTheme.color.policyColor,
            )
            Spacer(modifier = Modifier.height(AppTheme.spacer.heightDash))

            // Email input box style similar to screenshot
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = resetState.value.otp,
                    onValueChange = {
                        viewmodel.onOtpChanged(it)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(AppTheme.dimen.heightLargeButton)
                        ,
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.otp),
                            contentDescription = "Email",
                            modifier = Modifier.size(AppTheme.dimen.iconLargeSize)

                        )
                    },
                    placeholder = {
                        Text(
                            text = "Enter your OTP",
                            style = AppTheme.typography.placeHolder,
                            color = AppTheme.color.policyColor
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                    ),

                    shape = AppTheme.corner.buttonCorner
                )

            }
            Spacer(modifier = Modifier.height(AppTheme.spacer.heightDash))

            Text(
                text = "Enter new password",
                style = AppTheme.typography.placeHolder,
                color = AppTheme.color.policyColor,
            )

            Spacer(modifier = Modifier.height(AppTheme.spacer.heightDash))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = resetState.value.password,
                    onValueChange = {
                        viewmodel.onPasswordChanged(it)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(AppTheme.dimen.heightLargeButton),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_lock),
                            contentDescription = "pass",
                            modifier = Modifier.size(AppTheme.dimen.iconLargeSize)

                        )
                    },
                    placeholder = {
                        Text(
                            text = "Enter your new password",
                            style = AppTheme.typography.placeHolder,
                            color = AppTheme.color.policyColor
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            viewmodel.resetPassword(email)
                            keyBoard?.hide()
                        }
                    ),

                    shape = AppTheme.corner.buttonCorner
                )

            }

            Spacer(modifier = Modifier.height(AppTheme.spacer.heightDash))

            // Submit button (rounded, blue)
            Button(
                onClick = {  viewmodel.resetPassword(email)
                    keyBoard?.hide()},
                enabled = !resetState.value.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppTheme.dimen.heightLargeButton),
                shape = AppTheme.corner.buttonCorner,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if(resetState.value.isLoading) AppTheme.color.unEnableButton else AppTheme.color.largeButton, // blue like screenshot
                    contentColor = AppTheme.color.textButtonColor
                )
            ) {
                if (resetState.value.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(AppTheme.dimen.iconLargeSize),
                        strokeWidth = AppTheme.dimen.strokeWidth,
                        color = AppTheme.color.textButtonColor
                    )
                }
                Text(text = "Submit", style = AppTheme.typography.largeButtonTitle,color=  AppTheme.color.textButtonColor)
            }

        }

        if (resetState.value.showDialog) {
            InfoDialog(
                modifier= Modifier.align(Alignment.Center),
                isSuccess = resetState.value.isSuccess,
                resetState.value.status,
                resetState.value.message?:"",
                onDismiss = {
                    viewmodel.onDismissDialog()
                    if(resetState.value.isSuccess){
                        onSuccess(email,resetState.value.password)
                    }

                }
            )
        }
    }
}

