package com.project.mynoize.activities.signin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.mynoize.activities.signin.ui.theme.Color1

@Composable
fun CustomAlertDialog(
    onDismiss: () -> Unit,
    message: String = "This is the message"
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
                    text = if(message.contains("successfully"))  "Success" else "Warning",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
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
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Close",
                    color = Color.White,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .background(color = Color1, shape = RoundedCornerShape(22.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .clickable(onClick = onDismiss),
                )
            }
        }
    )
}