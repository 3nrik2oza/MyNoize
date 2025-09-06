package com.project.mynoize.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.mynoize.activities.main.ui.theme.LatoFontFamily
import com.project.mynoize.activities.main.ui.theme.NovaSquareFontFamily
import com.project.mynoize.activities.main.ui.theme.Red
import com.project.mynoize.activities.signin.ui.theme.Color1

@Composable
fun MessageAlertDialog(
    onDismiss: () -> Unit,
    message: String = "This is the message",
    warning: Boolean
){
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        modifier = Modifier,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ){
                Icon(imageVector = Icons.Default.Info, contentDescription = "")
                Text(
                    text = if(warning)  "WARNING" else "SUCCESS",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = LatoFontFamily
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = message,
                    fontSize = 15.sp,
                    fontFamily = NovaSquareFontFamily
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Close",
                    color = Color.White,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .background(color = Red, shape = RectangleShape)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .clickable(onClick = onDismiss),
                    fontFamily = LatoFontFamily
                )

            }
        }
    )
}

@Preview
@Composable
fun ShowMessageAlertDialog(){
    MessageAlertDialog(
        onDismiss = {},
        message = "This is the message",
        warning = true
    )

}