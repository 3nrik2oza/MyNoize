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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.mynoize.activities.main.ui.theme.NovaSquareFontFamily
import com.project.mynoize.core.presentation.components.Surrounding

@Composable
fun <T> CustomDropdown(
    modifier: Modifier = Modifier,
    itemList: List<T>,
    title: String = "Type",
    hint: String = "Select",
    selectedItem: T?,
    onItemClick: (T) -> Unit,
    displayText: (T) -> String = { it.toString() },
    onAddClick: () -> Unit = {},
    isError: Boolean,
    errorMessage: String = "",
    showDropdown: Boolean,
    addNew: Boolean = false,
    enabled: Boolean,
    onToggleShow: () -> Unit
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
                selectedItem = selectedItem,
                onItemClick = onItemClick,
                displayText = displayText,
                onAddClick = onAddClick,
                showDropdown = showDropdown,
                onToggleShow = onToggleShow,
                addNew = addNew,
                enabled = enabled,
                modifier = modifier
            )
        }
    }
}

@Composable
fun <T> DropdownList(
    itemList: List<T>,
    hint: String,
    selectedItem: T?,
    onItemClick: (T) -> Unit,
    displayText: (T) -> String = { it.toString() },
    onAddClick: () -> Unit,
    showDropdown: Boolean,
    onToggleShow: () -> Unit,
    addNew: Boolean,
    enabled: Boolean,
    modifier: Modifier,
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .border(width = 1.dp, color = if(enabled)Color.Black else Color.DarkGray, shape = RectangleShape)
            .background(color = Color.White, shape = RectangleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { if(enabled) onToggleShow() }
            ),
        contentAlignment = Alignment.Center
    ) {
        val textColor = when{
            selectedItem == null -> Color.LightGray
            !enabled -> Color.DarkGray
            else -> Color.Black
        }

        Text(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
            text = if(selectedItem == null) hint else displayText(selectedItem),
            fontFamily = NovaSquareFontFamily,
            color = textColor,
            textAlign = TextAlign.Start
        )
    }

    Box(contentAlignment = Alignment.Center, modifier = modifier.padding(1.dp)) {
        if (showDropdown) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
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
                if(addNew){
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clickable {
                                onAddClick()
                                onToggleShow()
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
                                onItemClick(item)
                                onToggleShow()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = displayText(item),
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

private data class DropdownItem(
    val id: Int,
    val name: String
)

@Preview(showBackground = true)
@Composable
private fun CustomDropdownPreview() {
    val items = listOf(
        DropdownItem(1, "Option 1"),
        DropdownItem(2, "Option 2"),
        DropdownItem(3, "Option 3")
    )

    CustomDropdown(
        itemList = items,
        title = "Type",
        hint = "Select Type",
        selectedItem = items.first(),
        onItemClick = {},
        displayText = { it.name },
        onAddClick = {},
        isError = false,
        showDropdown = false,
        onToggleShow = {},
        enabled = true,
        errorMessage = ""
    )
}