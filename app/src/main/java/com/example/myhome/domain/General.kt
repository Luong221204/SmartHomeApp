package com.example.myhome.domain

import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color

data class General(
    val iconId: Int = 0,
    val name: String,
    val addition: String?,
    val selectedColor: Color,
    val unselectedColor: Color,
    val background: Color,
    val unSelectedBackground: Color,
    val checked: MutableState<Boolean>,
    var onSwitch: (Boolean) -> Unit
)
