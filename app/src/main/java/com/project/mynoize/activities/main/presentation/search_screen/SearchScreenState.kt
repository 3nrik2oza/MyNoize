package com.project.mynoize.activities.main.presentation.search_screen

import com.project.mynoize.core.data.SearchItem

data class SearchScreenState(
    val searchQuery: String = "",
    val searchItems: List<SearchItem> = emptyList(),
    val isLoading: Boolean = false,
    val selectedSearchType: SearchTypes = SearchTypes.SONGS
)