package com.project.mynoize.di

import android.app.Application
import com.project.mynoize.activities.main.presentation.artist_screen.ArtistScreenViewModel
import com.project.mynoize.activities.main.presentation.create_artist.CreateArtistViewModel
import com.project.mynoize.activities.main.presentation.create_artist.domain.CreateArtistValidation
import com.project.mynoize.activities.main.presentation.create_playlist.CreatePlaylistViewModel
import com.project.mynoize.activities.main.presentation.create_playlist.domain.CreatePlaylistValidation
import com.project.mynoize.activities.main.presentation.create_song.CreateSongViewModel
import com.project.mynoize.activities.main.presentation.create_song.domain.CreateSongValidation
import com.project.mynoize.activities.main.presentation.favorite_screen.FavoriteScreenViewModel
import com.project.mynoize.activities.main.presentation.main_screen.MainScreenViewModel
import com.project.mynoize.activities.main.presentation.playlist_screen.PlaylistScreenViewModel
import com.project.mynoize.activities.main.presentation.select_songs_screen.SelectSongsViewModel
import com.project.mynoize.core.data.repositories.AlbumRepository
import com.project.mynoize.core.data.repositories.ArtistRepository
import com.project.mynoize.core.data.repositories.SongRepository
import com.project.mynoize.core.data.repositories.StorageRepository
import com.project.mynoize.activities.signin.SignInViewModel
import com.project.mynoize.activities.signin.SignUpViewModel
import com.project.mynoize.activities.signin.domain.SignInValidation
import com.project.mynoize.activities.signin.domain.SignUpValidation
import com.project.mynoize.core.data.AuthRepository
import com.project.mynoize.core.data.repositories.PlaylistRepository
import com.project.mynoize.core.data.repositories.UserRepository
import com.project.mynoize.managers.ExoPlayerManager
import com.project.mynoize.util.UserInformation
import org.koin.core.module.dsl.viewModel
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
        UserRepository(get())
    }

    single { PlaylistRepository(get(), get()) }

    single { AlbumRepository() }
    single { CreateSongValidation() }

    single { CreatePlaylistValidation() }

    single { SignInValidation() }

    single { SignUpValidation() }

    single { CreateArtistValidation() }

    single { UserInformation(context = get<Application>()) }

    single { ExoPlayerManager(context = get<Application>(), get()) }


    viewModel { ArtistScreenViewModel(get(), get(), get(), get()) }

    viewModel { MainScreenViewModel(get(), get(), get(), get(), get()) }

    viewModel { FavoriteScreenViewModel(get(), get()) }

    viewModel { PlaylistScreenViewModel(get(), get(), get(), get(), get(), get()) }

    viewModel { SelectSongsViewModel(get(), get()) }

    viewModel{ CreatePlaylistViewModel(get(), get(), get(), get()) }

    viewModel { CreateSongViewModel(get(), get(), get(), get(), get(), get()) }

    viewModel { CreateArtistViewModel(get(), get(), get(), get()) }

    viewModel { SignInViewModel(get(), get()) }

    viewModel { SignUpViewModel(get(), get()) }
}