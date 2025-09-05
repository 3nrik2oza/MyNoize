package com.project.mynoize.activities.main.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

@Composable
fun CustomDropdown(
    itemList: List<String>,
    title: String = "Type",
    hint: String = "Select",
    selectedIndex: Int,
    onItemClick: (Int) -> Unit,
    canAdd: Boolean = false,
    onAddClick: () -> Unit = {},
    isError: Boolean,
    errorMessage: String = ""
) {
    Column {
        Text(text = title)
        DropdownList(
            itemList = itemList,
            hint = hint,
            selectedIndex = selectedIndex,
            onItemClick = onItemClick,
            canAdd = canAdd,
            onAddClick = onAddClick
        )
        if(isError){
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun DropdownList(
    itemList: List<String>,
    hint: String,
    selectedIndex: Int,
    onItemClick: (Int) -> Unit,
    canAdd: Boolean,
    onAddClick: () -> Unit
) {
    var showDropdown by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(color = Color.Black, shape = RoundedCornerShape(23.dp))
            .padding(1.dp)
            .background(color = Color.White, shape = RoundedCornerShape(22.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { if(!itemList.isEmpty() || canAdd) {showDropdown = !showDropdown} }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = if(selectedIndex == -1) hint else itemList[selectedIndex],
            textAlign = TextAlign.Center
        )
    }

    Box(contentAlignment = Alignment.Center) {
        if (showDropdown) {
            Popup(
                alignment = Alignment.TopCenter,
                properties = PopupProperties(excludeFromSystemGesture = true),
                onDismissRequest = { showDropdown = false }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 250.dp)
                        .background(color = Color.Black, shape = RoundedCornerShape(23.dp))
                        .padding(1.dp)
                        .background(color = Color.White, shape = RoundedCornerShape(22.dp))
                        .verticalScroll(state = scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    if(canAdd){
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clickable {
                                    onAddClick()
                                    showDropdown = !showDropdown
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Add new album",
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }

                        HorizontalDivider(
                            thickness = 1.dp,
                            color = Color.LightGray
                        )
                    }


                    itemList.forEachIndexed { index, item ->
                        if (index != 0) {
                            HorizontalDivider(
                                thickness = 1.dp,
                                color = Color.LightGray
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clickable {
                                    onItemClick(index)
                                    showDropdown = !showDropdown
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
