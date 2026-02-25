package com.example.myhome.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myhome.domain.User
import com.example.myhome.domain.response.NetworkResult
import com.example.myhome.domain.response.Result
import com.example.myhome.local.DataManager
import com.example.myhome.local.DataManager2
import com.example.myhome.network.ApiConnect
import com.example.myhome.network.FcmToken
import com.example.myhome.repository.AuthRepository
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LoginViewmodel @Inject constructor(
    private val authRepository: AuthRepository,
    private val local : DataManager2
) : ViewModel() {
    private val _uiLoginState2 = MutableSharedFlow<LoginUiState>(replay = 1)
    val uiLoginState2 = _uiLoginState2.asSharedFlow()
    init {
        _uiLoginState2.tryEmit(
            LoginUiState()
        )
    }
    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiLoginState = MutableStateFlow(LoginUiState())
    val uiLoginState = _uiLoginState.asStateFlow()




    private val _uiResetState = MutableStateFlow(ResetPasswordUiState())
    val uiResetState = _uiResetState.asStateFlow()

    fun onEmailChanged(email: String){
        _uiLoginState2.tryEmit(
            uiLoginState2.replayCache[0].copy(email = email)
        )
        _uiLoginState.update {
            it.copy(email = email)
        }
    }
    fun onPasswordChangedInLoginScreen(password: String) {
        _uiLoginState.update {
            it.copy(password = password)
        }
    }
    fun login(){
        val email = _uiLoginState.value.email
        val password = _uiLoginState.value.password
        val scope = CoroutineScope(Dispatchers.IO)
        viewModelScope.launch {
            _uiLoginState.update { it.copy(isLoading = true, showDialog = null) }
            when(val r = authRepository.login(User(email = email, password = password))){
                is NetworkResult.Error -> {
                    _uiLoginState.update {
                        it.copy(isLoading = false, message = r.message , isSuccess = false, showDialog = true)
                    }
                }
                is NetworkResult.Success<User> -> {
                    Log.d("DUCLUONG", r.data.toString())

                    val user = r.data
                    local.saveUser(user)
                    local.saveLoginStatus(true)
                    _uiLoginState.update {
                        it.copy(isLoading = false, isSuccess = true)
                    }
                    getFcmToken(scope)
                }
                else -> {}
            }
        }
    }

    private fun getFcmToken(scope: CoroutineScope){
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e("FCM", "Lấy token thất bại", task.exception)
                    _uiLoginState.update {
                        it.copy(isLoading = false, isSuccess = false)
                    }
                    return@addOnCompleteListener
                }

                scope.launch {
                    val token = task.result
                    Log.d("FCM", "FCM Token: $token")
                    local.saveFcmToken(token)
                    val r = authRepository.updateFcmToken(FcmToken(token,
                        local.getUser().id))
                    _uiLoginState.update {
                        it.copy(isLoading = false, isSuccess = true)
                    }                }.invokeOnCompletion {
                    if(it == null) {
                        Log.d("FCM", "Gửi token thành công")
                    }
                }
            }
    }
    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, error = null, isEmailError = null ) }
    }

    fun dismissDialog() {
        _uiState.update { it.copy(showDialog = false) }
    }


    fun sendOtp() {
        val email = _uiState.value.email
        val emailError = when {
            email.isBlank() -> "Please enter your email"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email"
            else -> null
        }
        if(emailError != null) {
            _uiState.update { it.copy(isEmailError = emailError) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = authRepository.forgot(User(email = email))) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = result.message,
                        showDialog = true
                    ) }
                }
                else -> {}
            }
        }
    }

    fun onOtpChanged(otp: String){
        _uiResetState.update { it.copy(otp = otp) }
    }

    fun onPasswordChanged(password: String) {
        _uiResetState.update {
            it.copy(password = password)
        }
    }
    fun onDismissDialog(){
        _uiResetState.update { it.copy(showDialog = false) }
    }

    fun resetPassword(email: String){
        val otp = _uiResetState.value.otp
        val password = _uiResetState.value.password
        viewModelScope.launch {
            _uiResetState.update { it.copy(isLoading = true ) }
            when(val r = authRepository.resetAfterForgot(User(otp = otp, newPassword = password, email = email))){
                is NetworkResult.Success -> {
                    _uiResetState.update { it.copy(isLoading = false, isSuccess = true, showDialog = true , message = r.data.message) }
                }
                is NetworkResult.Error -> {
                    _uiResetState.update { it.copy(
                        isLoading = false,
                        message = r.message,
                        showDialog = true
                    ) }
                }
                else -> {}
            }
        }
    }

}

data class ForgotPasswordUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val status: Boolean = false,
    val isSuccess: Boolean = false,
    val isEmailError: String? =null,
    val showDialog: Boolean = false
)

data class ResetPasswordUiState(
    val isLoading: Boolean = false,
    val status: Boolean = false,
    val isSuccess: Boolean = false,
    val password: String = "",
    val otp: String = "",
    val message:String? = null,
    val showDialog: Boolean = false
)

data class LoginUiState(
    val isLoading: Boolean=false,
    val isSuccess: Boolean? = null,
    val password: String = "",
    val email: String = "",
    val message:String? = null,
    val showDialog: Boolean? = false
)