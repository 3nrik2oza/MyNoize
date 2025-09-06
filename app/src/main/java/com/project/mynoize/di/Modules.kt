package com.project.mynoize.di

import com.project.mynoize.activities.main.presentation.create_song.CreateSongViewModel
import com.project.mynoize.activities.main.presentation.create_song.domain.CreateSongValidation
import com.project.mynoize.activities.main.repository.AlbumRepository
import com.project.mynoize.activities.main.repository.ArtistRepository
import com.project.mynoize.activities.main.repository.SongRepository
import com.project.mynoize.activities.main.repository.StorageRepository
import com.project.mynoize.activities.signin.SignInViewModel
import com.project.mynoize.activities.signin.domain.SignInValidation
import com.project.mynoize.core.data.AuthRepository
import org.koin.core.module.dsl.viewModel
import org.koin.core.scope.get
import org.koin.dsl.module

val appModule = module{
    single{
        AuthRepository()
    }
    single{
        ArtistRepository()
    }
    single{
        StorageRepository()
    }
    single {
        SongRepository()
    }
    single {
        AlbumRepository()
    }
    single {
        CreateSongValidation()
    }

    single {
        SignInValidation()
    }

    viewModel {
        SignInViewModel(get(), get())
    }

    viewModel {
        CreateSongViewModel(get(), get(), get(), get(), get(), get())
    }
}