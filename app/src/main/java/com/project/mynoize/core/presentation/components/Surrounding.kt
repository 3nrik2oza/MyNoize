package com.project.mynoize.core.presentation.components



import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.project.mynoize.activities.main.ui.theme.NovaSquareFontFamily
import com.project.mynoize.activities.main.ui.theme.Red


@Composable
fun Surrounding(
    modifier: Modifier = Modifier,
    title: String = "",
    isError: Boolean = false,
    errorMessage: String = "",
    content: @Composable () -> Unit
){
    Column(
        modifier = modifier
    ) {
        Text(
            text = title,
            fontFamily = NovaSquareFontFamily,
            color = Red
        )

        Spacer(modifier= Modifier.height(5.dp))
        content()

        if(isError){
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = errorMessage,
                color = Color.Black,
                fontFamily = NovaSquareFontFamily
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
    }

}