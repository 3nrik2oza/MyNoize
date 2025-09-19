package com.project.mynoize.core.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.project.mynoize.activities.main.ui.theme.DarkGray
import com.project.mynoize.activities.main.ui.theme.LightGray
import com.project.mynoize.activities.main.ui.theme.NovaSquareFontFamily

@Composable
fun CustomSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onImeSearch: () -> Unit,
    modifier: Modifier = Modifier
){

    CompositionLocalProvider(
        LocalTextSelectionColors provides TextSelectionColors(
            handleColor = Color.White,
            backgroundColor = Color.White
        )
    ) {
        OutlinedTextField(
            value =searchQuery,
            onValueChange = onSearchQueryChange,
            shape = RectangleShape,
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = DarkGray,
                focusedBorderColor = LightGray,
                unfocusedBorderColor = Color.Black
            ),
            placeholder = {
                Text(
                    text = "Search...",
                    fontFamily = NovaSquareFontFamily
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search bar Icon"
                )
            },
            singleLine = true,
            keyboardActions = KeyboardActions(
                onSearch = {
                    onImeSearch
                }
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            trailingIcon = {
                AnimatedVisibility(
                    visible = searchQuery.isNotBlank()
                ) {
                    IconButton(
                        onClick = {onSearchQueryChange("")}
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear search",
                        )
                    }
                }
            },
            modifier = modifier
                .background(
                    shape = RectangleShape,
                    color = Color.White
                )
                .minimumInteractiveComponentSize()
        )
    }

}

@Preview
@Composable
fun ShowCustomSearchBar(){
    CustomSearchBar(
        searchQuery = "",
        onSearchQueryChange = {},
        onImeSearch = {}
    )
}