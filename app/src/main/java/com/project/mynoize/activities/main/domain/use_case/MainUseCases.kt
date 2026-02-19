package com.project.mynoize.activities.main.domain.use_case

data class MainUseCases(
    val restoreLastSessionUseCase: RestoreLastSessionUseCase,
    val syncFavoritePlaylistUseCase: SyncFavoritePlaylistUseCase,
    val syncFavoriteSongsUseCase: SyncFavoriteSongsUseCase,
    val syncFavoriteAlbumsUseCase: SyncFavoriteAlbumsUseCase,
    val removeLocalNonFavoriteSongsUseCase: RemoveLocalNonFavoriteSongsUseCase
)
