package com.example.myhome.view

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
import com.example.myhome.domain.Password
import com.example.myhome.domain.response.Result
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

        viewmodel.password
        setContent {

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
                modifier = Modifier.fillMaxWidth().height(50.dp).background(color = Color.White).padding(start = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(
                    painter = painterResource(R.drawable.back),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp).clickable{
                        onBack()
                    }
                )
                Spacer(Modifier.width(20.dp))
                Text( text = "Thay đổi mật khẩu", style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold))
            }
            Spacer(Modifier.height(24.dp))

            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
                    Text("Mật khẩu hiện tại :", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(24.dp))
                    when(showOld.value){
                        is Result.Loading ->{
                            CircularProgressIndicator(
                                modifier= Modifier.size(16.dp),
                                color = Color.Gray
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
                                    fontSize = 14.sp,
                                    color = Color.Black.copy(0.6f)
                                )
                            } else {
                                Text("No password", fontSize = 14.sp, color = Color.Gray)
                            }
                        }
                        is Result.Error->{
                            Log.d("DUCLUONG","lỗi")
                        }
                    }

                }


                Spacer(Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { input ->
                            val filtered = input.filter { it.isDigit() }
                            newPassword = filtered
                        },
                        label = { Text("Mật khẩu mới") },
                        singleLine = true,
                        modifier = Modifier.width(300.dp),
                        visualTransformation = if (showNew) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showNew = !showNew }) {
                                Icon(
                                    painter = painterResource( if (showNew) R.drawable.visibility else R.drawable.visibility_off),
                                    contentDescription = "Show/Hide",
                                    modifier = Modifier.size(24.dp)

                                )
                            }
                        },
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
                    Spacer(Modifier.width(24.dp))
                    if(isPending){
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.Gray
                        )
                    }else{
                        Icon(
                            painter = painterResource( R.drawable.send),
                            contentDescription = "Show/Hide",
                            modifier = Modifier.size(24.dp).clickable(
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
                            tint = if(newPassword.isEmpty()) Color.Black.copy(0.5f) else Color.Black

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
        modifier = modifier.width(250
            .dp)
            .wrapContentHeight(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hình minh họa
            Image(
                painter = painterResource(id = R.drawable.pass), // thay bằng icon bạn có
                contentDescription = "password",
                modifier = Modifier.size(64.dp)
            )
            // Mô tả
            Text(
                text = if(isSuccess) "Thành công !" else "Thất bại",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )


            // Nút chính
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0066FF))
            ) {
                Text(
                    text = "Ok",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}
