package com.project.mynoize.activities.signin.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.project.mynoize.activities.main.ui.theme.NovaSquareFontFamily
import com.project.mynoize.activities.signin.ui.theme.AppTextInputColors
import com.project.mynoize.activities.signin.ui.theme.ModifiedTextInputColors
import com.project.mynoize.core.presentation.components.Surrounding

@Composable
fun CustomPasswordTextField(
    modifier: Modifier = Modifier,
    title: String,
    hintText: String,
    inputValue: String,
    numberOfLines: Int = 1,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    errorMessage: String = ""
){
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Surrounding(
        modifier = modifier.fillMaxWidth(),
        title = title,
        isError = isError,
        errorMessage = errorMessage
    ){
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            colors = ModifiedTextInputColors,
            value = inputValue,
            shape = RectangleShape,
            placeholder = { Text(text=hintText, fontFamily = NovaSquareFontFamily, color = Color.LightGray) },
            maxLines = numberOfLines,
            onValueChange = onValueChange,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.VisibilityOff
                else Icons.Filled.Visibility

                // Please provide localized description for accessibility services
                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = {passwordVisible = !passwordVisible}){
                    Icon(imageVector  = image, description)
                }
            },
            textStyle = TextStyle.Default.copy(fontFamily = NovaSquareFontFamily)
        )
    }
/*
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(background = Color.White)
    ) {
        Column {
            Text(text = title)
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(25.dp),
                colors = AppTextInputColors,
                value = inputValue,
                placeholder = { Text(hintText) },
                maxLines = numberOfLines,
                onValueChange = onValueChange,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = isError,
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.VisibilityOff
                    else Icons.Filled.Visibility

                    // Please provide localized description for accessibility services
                    val description = if (passwordVisible) "Hide password" else "Show password"

                    IconButton(onClick = {passwordVisible = !passwordVisible}){
                        Icon(imageVector  = image, description)
                    }
                }
            )
            if(isError){
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }*/


}