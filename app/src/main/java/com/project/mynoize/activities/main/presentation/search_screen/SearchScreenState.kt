package com.project.mynoize.activities.main.presentation.search_screen

import com.project.mynoize.core.domain.entities.Artist
import com.project.mynoize.core.domain.entities.Playlist
import com.project.mynoize.core.domain.entities.SearchItem

data class SearchScreenState(
    val searchQuery: String = "",
    val searchItems: List<SearchItem> = emptyList(),
    val isSheetOpen: Boolean = false,
    val selectedSong: SearchItem.SongItem? = null,
    val selectedArtist: Artist? = null,
    val selectPlaylistSheet: Boolean = false,
    val userPlaylists: List<Playlist> = emptyList(),
    val isLoading: Boolean = true,
    val isInvisibleLoading: Boolean = false,
    val selectedSearchType: SearchTypes = SearchTypes.SONGS
)