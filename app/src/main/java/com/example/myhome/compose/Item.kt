package com.example.myhome.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myhome.domain.response.Result
import com.example.myhome.ui.theme.AppTheme
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

@Composable
fun Device(
    checked: MutableState<Boolean>,
    modifier: Modifier,
    iconId :Int,
    name:String,
    addition:String?,
    selectedColor: Color,
    unSelectedColor: Color = Color.Black,
    background:Color = Color.Red.copy(0.3f),
    unSelectedBack:Color =Color.Black.copy(0.1f),
    onSwitch:(Boolean)->Unit,
    onNextActivity:()->Unit = {},
    sharedFlow: SharedFlow<Result>? = null
) {

    // Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutine = rememberCoroutineScope()

    // State từ SharedFlow
    val resultState = sharedFlow?.collectAsState(Result.Nothing)

    var disable by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }


    when ( resultState?.value) {

        is Result.Nothing -> {
            isLoading = false
            disable = false
            isError = false
        }

        is Result.Loading -> {
            isLoading = true
            disable = true
            isError = false
        }

        is Result.Error -> {
            isLoading = false
            disable = false
            isError = true

            coroutine.launch {
                snackbarHostState.showSnackbar(
                    message =  "Đã xảy ra lỗi!",
                    withDismissAction = true
                )
            }
        }

        is Result.Response<*> -> {
            isLoading = false
            disable = false
            isError = false
        }

        else -> {}
    }

    val blockInteraction = if (isLoading) {
        Modifier
            .alpha(0.6f)
            .pointerInput(Unit) {}
    } else Modifier


    Box {
        Column(
            modifier = modifier
                .clickable{
                    onNextActivity()
                }
                .then(blockInteraction)
                .clip(AppTheme.corner.buttonCorner)
                .background(if (checked.value) background else unSelectedBack)
        ) {

            Spacer(Modifier.height(AppTheme.spacer.smallGap))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppTheme.dimen.heightInsideDevice),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacer.largeGap),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(iconId),
                    contentDescription = null,
                    modifier = Modifier.size(AppTheme.dimen.iconHugeSize),
                    tint = if (checked.value) selectedColor else unSelectedColor
                )
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(AppTheme.dimen.iconLargeSize),
                        strokeWidth = AppTheme.dimen.strokeWidth,
                        color = if (checked.value) selectedColor else unSelectedColor
                    )
                }

                Switch(
                    checked = checked,
                    switch = {
                        if (!disable) {
                            onSwitch(it)
                        }
                    }
                )
            }

            Spacer(Modifier.height(AppTheme.spacer.smallGap))

            Box(
                modifier = Modifier
                    .height(AppTheme.dimen.heightLargeButton)
                    .padding(start = AppTheme.padding.insidePadding),
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                ) {

                    Text(
                        text = name,
                        style = AppTheme.typography.deviceLargeTitle
                    )

                    addition?.let {
                        Spacer(Modifier.height(AppTheme.spacer.smallGap))
                        Text(
                            text = it,
                            style = AppTheme.typography.deviceSmallTitle
                        )
                    }
                }
            }
        }

        // Snackbar hiển thị lỗi
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}



