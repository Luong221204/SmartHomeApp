package com.example.myhome.domain.device

import android.app.Activity
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import com.example.myhome.domain.response.Result
import kotlinx.coroutines.flow.MutableSharedFlow

data class General(
    val iconId: Int = 0,
    val name: String,
    val addition: String?,
    val selectedColor: Color,
    val unselectedColor: Color,
    val background: Color,
    val unSelectedBackground: Color,
    val checked: MutableState<Boolean>,
    val statusResponse: MutableSharedFlow<Result>? = null,
    var activity : Class<out Activity>? = null,
    var onSwitch: (Boolean) -> Unit,

    )
