package com.project.mynoize.activities.signin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.mynoize.activities.signin.ui.theme.Color1

@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    fontSize : TextUnit = 25.sp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onClick,
            modifier = Modifier
                .background(color = Color.Black, shape = RoundedCornerShape(23.dp))
                .padding(1.dp)
                .background(color = Color1, shape = RoundedCornerShape(22.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
        ) {
            Text(
                text = text,
                fontSize = fontSize,
                modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp),
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}