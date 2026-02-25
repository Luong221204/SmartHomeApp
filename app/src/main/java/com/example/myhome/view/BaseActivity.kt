package com.example.myhome.view

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.myhome.domain.User
import com.example.myhome.local.DataManager
import com.example.myhome.network.ApiConnect
import com.example.myhome.network.AuthEvent
import com.example.myhome.network.AuthEventBus
import com.example.myhome.network.FcmToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
open class BaseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                AuthEventBus.authEvent.collect { event ->
                    ApiConnect.service!!.deleteFcmToken(
                        FcmToken(
                            DataManager.getFcmToken(),
                            DataManager.getUser().id
                        )
                    )
                    DataManager.saveLoginStatus(false)
                    DataManager.saveUser(User())
                    DataManager.saveFcmToken("")
                    handleAuthEvent(event)
                }
            }
        }
    }
    private fun handleAuthEvent(event: AuthEvent) {
        Toast.makeText(
            this,
            event.message,
            Toast.LENGTH_LONG
        ).show()
        navigateToLogin()
    }

    private fun navigateToLogin() {
        startActivity(
            Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
    }
}