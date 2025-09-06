package com.project.mynoize.core.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import com.project.mynoize.activities.main.ui.theme.LightGray
import com.project.mynoize.activities.main.ui.theme.NovaSquareFontFamily
import com.project.mynoize.activities.signin.ui.theme.ModifiedTextInputColors

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    title: String,
    hintText: String,
    inputValue: String,
    numberOfLines: Int = 1,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    errorMessage: String = ""

) {

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
            placeholder = { Text(text = hintText, fontFamily = NovaSquareFontFamily, color = Color.LightGray) },
            maxLines = numberOfLines,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.None),
            textStyle = TextStyle.Default.copy(fontFamily = NovaSquareFontFamily)
        )
    }
}

@Preview(showBackground = false)
@Composable
fun ShowCustomTextField(){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        CustomTextField(
            title = "Name",
            hintText = "Your name",
            inputValue = "",
            onValueChange = {},
            isError = false
        )
    }
}
