package com.project.mynoize.activities.main.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.project.mynoize.activities.main.ui.theme.NovaSquareFontFamily
import com.project.mynoize.core.presentation.components.Surrounding

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
    Surrounding(
        title = title,
        isError = isError,
        errorMessage = errorMessage
    ) {
        Column {
            DropdownList(
                itemList = itemList,
                hint = hint,
                selectedIndex = selectedIndex,
                onItemClick = onItemClick,
                canAdd = canAdd,
                onAddClick = onAddClick
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
            .border(width = 1.dp, color = Color.Black, shape = RectangleShape)
            .background(color = Color.White, shape = RectangleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { if(!itemList.isEmpty() || canAdd) {showDropdown = !showDropdown} }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
            text = if(selectedIndex == -1) hint else itemList[selectedIndex],
            fontFamily = NovaSquareFontFamily,
            color = if(selectedIndex == -1)Color.LightGray else Color.Black,
            textAlign = TextAlign.Start
        )
    }

    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 15.dp)) {
        if (showDropdown) {
            Popup(
                alignment = Alignment.TopCenter,
                properties = PopupProperties(excludeFromSystemGesture = true),
                onDismissRequest = { showDropdown = false },

            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .heightIn(max = 250.dp)
                        .drawBehind {
                            // custom border: left, right, bottom only
                            val strokeWidth = (4/2).dp.toPx()
                            val color = Color.Black
                            val width = size.width
                            val height = size.height

                            // left
                            drawLine(color, start = Offset(0f, 0f), end = Offset(0f, height), strokeWidth)
                            // right
                            drawLine(color, start = Offset(width, 0f), end = Offset(width, height), strokeWidth)
                            // bottom
                            drawLine(color, start = Offset(0f, height), end = Offset(width, height), strokeWidth)
                        }
                        .background(color = Color.White, shape = RectangleShape)
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
                                textAlign = TextAlign.Center,
                                fontFamily = NovaSquareFontFamily
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
                                textAlign = TextAlign.Center,
                                fontFamily = NovaSquareFontFamily
                            )
                        }
                    }
                }
            }
        }
    }
}
