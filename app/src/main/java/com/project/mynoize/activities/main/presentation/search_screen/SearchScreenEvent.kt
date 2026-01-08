package com.project.mynoize.activities.main.presentation.search_screen

import com.project.mynoize.core.data.SearchItem

sealed interface SearchScreenEvent {

    data class OnSearchQueryChange(val query: String) : SearchScreenEvent
    data class OnSearchTypeChange(val type: SearchTypes) : SearchScreenEvent

    data class OnSearchItemFavoriteClicked(val item: SearchItem) : SearchScreenEvent
    data class OnSearchItemClicked(val item: SearchItem) : SearchScreenEvent

}