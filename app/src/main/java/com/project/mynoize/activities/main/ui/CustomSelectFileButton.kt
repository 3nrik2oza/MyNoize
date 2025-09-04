package com.project.mynoize.activities.main.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CustomSelectFileButton(
    text: String = "Select song",
    onClick: () -> Unit,
    isError: Boolean = false,
    errorMessage: String = ""
){

    Column {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(color = Color.Black, shape = RoundedCornerShape(23.dp))
                .padding(1.dp)
                .background(color = Color.White, shape = RoundedCornerShape(22.dp))
                .clickable{
                    onClick()
                }
        ){
            Text(
                text = text,
                maxLines = 2,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
        }


        if(isError){
            Spacer(Modifier.height(5.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Preview(showBackground = false)
@Composable
fun ShowCustomSelectFileButton(){
    CustomSelectFileButton(
        onClick = {}
    )

}