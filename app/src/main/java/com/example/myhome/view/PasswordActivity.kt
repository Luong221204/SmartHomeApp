package com.example.myhome.view

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.myhome.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import com.example.myhome.domain.device.Password
import com.example.myhome.domain.response.Result
import com.example.myhome.ui.theme.AppTheme
import com.example.myhome.viewmodel.PasswordViewmodel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class PasswordActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewmodel : PasswordViewmodel by viewModels()
        setContent {
            AppTheme{
                Scaffold() {
                        contentPadding->
                    SimpleChangePasswordScreen(
                        Modifier.padding(contentPadding).fillMaxSize(),viewmodel){
                        finish()
                    }
                }
            }
            }

    }
}
@Composable
fun SimpleChangePasswordScreen(
    modifier: Modifier,
    viewmodel: PasswordViewmodel,
    onBack:()->Unit
) {
    val focusManager = LocalFocusManager.current
    val keyBoard = LocalSoftwareKeyboardController.current
    val coroutine = CoroutineScope(Dispatchers.IO+ SupervisorJob())
    var isPending by remember { mutableStateOf(false) }
    var newPassword by remember { mutableStateOf("") }
    val showOld =viewmodel.password.collectAsState()
    var showNew by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }
    Box(modifier = modifier){
        Column(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().height(AppTheme.dimen.heightLargeButton).background(color = AppTheme.color.backgroundAppColor).padding(start = AppTheme.padding.smallHorizontalPadding),
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(
                    painter = painterResource(R.drawable.back),
                    contentDescription = null,
                    modifier = Modifier.size(AppTheme.dimen.iconLargeSize).clickable{
                        onBack()
                    }
                )
                Spacer(Modifier.width(AppTheme.spacer.heightDash))
                Text( text = "Thay đổi mật khẩu", style = AppTheme.typography.introSectionTitle)
            }
            Spacer(Modifier.height(AppTheme.spacer.heightDash))

            Column(
                modifier = Modifier.fillMaxWidth().padding(AppTheme.padding.smallHorizontalPadding)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
                    Text("Mật khẩu hiện tại :", style = AppTheme.typography.introSectionTitle)
                    Spacer(Modifier.width(AppTheme.spacer.heightDash))
                    when(showOld.value){
                        is Result.Loading ->{
                            CircularProgressIndicator(
                                modifier= Modifier.size(AppTheme.dimen.iconSmallSize),
                                strokeWidth = AppTheme.dimen.strokeWidth,
                                color = AppTheme.color.unEnableButton
                            )
                        }
                        is Result.Nothing ->{}
                        is Result.Response<*> -> {
                            val response = (showOld.value as Result.Response<*>).t
                            // safe cast Response<Password>
                            val content = (response as? Response<Password>)?.body()?.password
                            if (content != null) {
                                Text(
                                    text = content,
                                    style = AppTheme.typography.placeHolder,
                                    color = AppTheme.color.policyColor
                                )
                            } else {
                                Text("No password", style = AppTheme.typography.placeHolder,
                                    color = AppTheme.color.policyColor)
                            }
                        }
                        is Result.Error->{
                            Log.d("DUCLUONG","lỗi")
                        }
                    }

                }


                Spacer(Modifier.height(AppTheme.spacer.heightDash))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { input ->
                            val filtered = input.filter { it.isDigit() }
                            newPassword = filtered
                        },
                        label = { Text("Mật khẩu mới",style = AppTheme.typography.placeHolder,color = AppTheme.color.policyColor) },
                        singleLine = true,
                        modifier = Modifier.width(AppTheme.dimen.widthLargeSize).height(AppTheme.dimen.heightLargeButton),
                        visualTransformation = if (showNew) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showNew = !showNew }) {
                                Icon(
                                    painter = painterResource( if (showNew) R.drawable.visibility else R.drawable.visibility_off),
                                    contentDescription = "Show/Hide",
                                    modifier = Modifier.size(AppTheme.dimen.iconLargeSize)

                                )
                            }
                        },
                        shape = AppTheme.corner.buttonCorner,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = if(newPassword.isEmpty())ImeAction.Done else ImeAction.Send
                        ),
                        keyboardActions = KeyboardActions (
                            onSend = {
                                focusManager.clearFocus()
                                coroutine.launch {
                                    withContext(Dispatchers.Main){
                                        isPending = true
                                    }
                                    val r = viewmodel.changePassword(newPassword)
                                    withContext(Dispatchers.Main){
                                        isSuccess = r
                                        showDialog = true
                                        if(r){
                                            newPassword=""
                                            isPending = false
                                        }
                                    }
                                }
                                keyBoard?.hide()

                            },
                            onDone = {
                                focusManager.clearFocus()
                                keyBoard?.hide()
                            }
                        )
                    )
                    Spacer(Modifier.width(AppTheme.spacer.heightDash))
                    if(isPending){
                        CircularProgressIndicator(
                            modifier = Modifier.size(AppTheme.dimen.iconLargeSize),
                            strokeWidth = AppTheme.dimen.strokeWidth,
                            color = AppTheme.color.policyColor
                        )
                    }else{
                        Icon(
                            painter = painterResource( R.drawable.send),
                            contentDescription = "Show/Hide",
                            modifier = Modifier.size(AppTheme.dimen.iconLargeSize).clickable(
                                enabled = newPassword.isNotEmpty(),
                                onClick = {
                                    focusManager.clearFocus()
                                    keyBoard?.hide()
                                    coroutine.launch {
                                        withContext(Dispatchers.Main){
                                            isPending = true
                                        }
                                        val r = viewmodel.changePassword(newPassword)
                                        withContext(Dispatchers.Main){
                                            isSuccess = r
                                            showDialog = true
                                            if(r){
                                                newPassword=""
                                                isPending = false
                                            }
                                        }
                                    }
                                }),
                            tint = if(newPassword.isEmpty()) AppTheme.color.unEnableButton else AppTheme.color.brown

                        )
                    }


                }

            }

        }

        if (showDialog) {
            ConfirmDialog(
                modifier= Modifier.align(Alignment.Center),
                isSuccess = isSuccess,
                onDismiss = { showDialog = false }
            )
        }
    }

}
@Composable
fun ConfirmDialog(
    modifier: Modifier,
    isSuccess: Boolean,
    onDismiss:()->Unit
) {
    Card(
        modifier = modifier.width(AppTheme.dimen.dialogSize)
            .wrapContentHeight(),
        shape = AppTheme.corner.dialogCorner,
    ) {
        Column(
            modifier = Modifier
                .background(AppTheme.color.backgroundAppColor)
                .padding(AppTheme.padding.largeHorizontalPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacer.heightDash)
        ) {
            Image(
                painter = painterResource(id = R.drawable.pass),
                contentDescription = "password",
                modifier = Modifier.size(AppTheme.dimen.iconHugeSize)
            )
            Text(
                text = if(isSuccess) "Thành công !" else "Thất bại",
                style  = AppTheme.typography.largeButtonTitle,
                textAlign = TextAlign.Center
            )


            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AppTheme.color.largeButton)
            ) {
                Text(
                    text = "Ok",
                    color = AppTheme.color.textButtonColor
                )
            }
        }
    }
}
