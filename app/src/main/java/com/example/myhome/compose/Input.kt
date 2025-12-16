package com.example.myhome.compose

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.myhome.R
import com.example.myhome.ui.theme.AppTheme

@Composable
fun RoundedInput(
    value: MutableState<String>,
    label: String,
    icon: Int,
    isPassword: Boolean = false,
    passwordVisible: MutableState<Boolean>? = null
) {
    OutlinedTextField(
        keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Email),
        value = value.value,
        onValueChange = { value.value = it },
        placeholder = { Text(label , style = AppTheme.typography.placeHolder, color = AppTheme.color.policyColor) },
        leadingIcon = {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(AppTheme.dimen.iconLargeSize),
                tint = AppTheme.color.largeButton
            )
        },
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = {
                    passwordVisible!!.value = !passwordVisible.value
                }) {
                    passwordVisible?.value?.let {
                        Icon(
                            painterResource(
                                id = if (it)
                                    R.drawable.visibility
                                else R.drawable.visibility_off
                            ),
                            contentDescription = "toggle password",
                            tint = AppTheme.color.largeButton,
                            modifier = Modifier.size(AppTheme.dimen.iconLargeSize),

                            )
                    }

                }
            }
        } else null,
        modifier = Modifier
            .fillMaxWidth()
            .height(AppTheme.dimen.heightLargeButton),
        shape = (AppTheme.corner.buttonCorner),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AppTheme.color.largeButton,
            unfocusedBorderColor = AppTheme.color.unEnableButton,

            ),
        singleLine = true,
        visualTransformation =
            if (isPassword && passwordVisible != null && !passwordVisible.value)
                PasswordVisualTransformation()
            else VisualTransformation.None
    )
}
