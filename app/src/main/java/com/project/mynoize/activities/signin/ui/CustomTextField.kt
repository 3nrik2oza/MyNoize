package com.project.mynoize.activities.signin.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.project.mynoize.activities.signin.ui.theme.AppTextInputColors

@Composable
fun CustomTextField(
    title: String,
    hintText: String,
    inputValue: String,
    numberOfLines: Int = 1,
    onValueChange: (String) -> Unit
) {
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
                placeholder = { Text(text = hintText) },
                maxLines = numberOfLines,
                onValueChange = onValueChange,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.None)
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}