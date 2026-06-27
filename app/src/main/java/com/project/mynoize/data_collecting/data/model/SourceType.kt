package com.project.mynoize.data_collecting.data.model

enum class SourceType(val displayName: String) {
    PLAYLIST("Playlist"),
    ALBUM("Album"),
    RECOMMENDATION("Recommendation"),
    ARTIST_PLAYLIST("Artist playlist"),
    SEARCH("Search");

    companion object {
        fun fromDisplayName(name: String): SourceType? =
            entries.find { it.displayName == name }
    }
}