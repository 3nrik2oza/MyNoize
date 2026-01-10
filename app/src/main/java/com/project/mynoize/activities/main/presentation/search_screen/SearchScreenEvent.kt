package com.project.mynoize.activities.main.presentation.search_screen

import com.project.mynoize.core.data.Playlist
import com.project.mynoize.core.data.SearchItem

sealed interface SearchScreenEvent {

    data class OnSearchQueryChange(val query: String) : SearchScreenEvent
    data class OnSearchTypeChange(val type: SearchTypes) : SearchScreenEvent

    data class OnSearchItemFavoriteClicked(val item: SearchItem) : SearchScreenEvent
    data class OnSearchItemClicked(val item: SearchItem) : SearchScreenEvent

    data class OnMoreOptionsSongClick(val selectedSong: SearchItem.SongItem) : SearchScreenEvent

    data class OnPlaylistSelected(val playlist: Playlist) : SearchScreenEvent

    object OnToggleMoreOptionsSheet: SearchScreenEvent
    object OnToggleSelectPlaylistSheet: SearchScreenEvent


    object OnUpdateSearchItems: SearchScreenEvent

}