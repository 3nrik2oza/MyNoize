package com.project.mynoize.di

import android.app.Application
import androidx.room.Room
import com.project.mynoize.activities.main.domain.use_case.DownloadMissingSongUseCase
import com.project.mynoize.activities.main.domain.use_case.MainUseCases
import com.project.mynoize.activities.main.domain.use_case.RemoveLocalNonFavoriteSongsUseCase
import com.project.mynoize.activities.main.domain.use_case.RestoreLastSessionUseCase
import com.project.mynoize.activities.main.domain.use_case.SyncFavoriteAlbumsUseCase
import com.project.mynoize.activities.main.domain.use_case.SyncFavoritePlaylistUseCase
import com.project.mynoize.activities.main.domain.use_case.SyncFavoriteSongsUseCase
import com.project.mynoize.activities.main.presentation.artist_screen.ArtistScreenViewModel
import com.project.mynoize.activities.main.presentation.create_artist.CreateArtistViewModel
import com.project.mynoize.activities.main.presentation.create_artist.domain.CreateArtistValidation
import com.project.mynoize.activities.main.presentation.create_playlist.CreatePlaylistViewModel
import com.project.mynoize.activities.main.presentation.create_playlist.domain.CreatePlaylistValidation
import com.project.mynoize.activities.main.presentation.create_song.CreateSongViewModel
import com.project.mynoize.activities.main.presentation.create_song.domain.CreateSongValidation
import com.project.mynoize.activities.main.presentation.favorite_screen.FavoriteScreenViewModel
import com.project.mynoize.activities.main.presentation.main_screen.MainScreenViewModel
import com.project.mynoize.activities.main.presentation.music_screen.MusicScreenViewModel
import com.project.mynoize.activities.main.presentation.playlist_screen.PlaylistScreenViewModel
import com.project.mynoize.activities.main.presentation.search_screen.SearchScreenViewModel
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
import com.project.mynoize.core.data.database.MusicDatabase
import com.project.mynoize.core.data.repositories.PlaylistRepository
import com.project.mynoize.core.data.repositories.UserRepository
import com.project.mynoize.managers.ExoPlayerManager
import com.project.mynoize.network.NetworkMonitor
import com.project.mynoize.util.UserInformation
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module{

    single{ AuthRepository() }


   // single{ ArtistRepository(get()) }
    //single{ StorageRepository() }
   // single { UserRepository(get()) }
   // single { PlaylistRepository(get(), get()) }
    //single { AlbumRepository(get()) }

    single { UserInformation(context = get<Application>()) }

    single { SignInValidation() }

    single { SignUpValidation() }

    viewModel { SignInViewModel(get(), get()) }

    viewModel { SignUpViewModel(get(), get()) }
}

val userScopeModule = module {
    scope(named("USER_SCOPE")) {

        scoped { AuthRepository() }
        scoped { UserRepository(get()) }
        scoped { ArtistRepository(get()) }
        scoped { SongRepository(get(), get()) }
        scoped { PlaylistRepository(get(), get(), get(), get()) }
        scoped { AlbumRepository(get(),get(), get()) }
        scoped { StorageRepository(get()) }

        scoped { CreateArtistValidation() }

        scoped { CreateSongValidation() }

        scoped { CreatePlaylistValidation() }

        scoped { NetworkMonitor(context = get<Application>()) }


        scoped {
            Room.databaseBuilder(
                get<Application>(),
                MusicDatabase::class.java,
                "music_database"
            )
                .fallbackToDestructiveMigration()
                .build()
        }

        scoped { get<MusicDatabase>().songDao }
        scoped { get<MusicDatabase>().albumDao }
        scoped { get<MusicDatabase>().artistDao }
        scoped { get<MusicDatabase>().playlistDao }

        //use cases
        scoped { RestoreLastSessionUseCase(get(), get()) }
        scoped { DownloadMissingSongUseCase(get(), get(), get()) }
        scoped { SyncFavoritePlaylistUseCase(get(),get(), get()) }
        scoped { SyncFavoriteSongsUseCase(get(), get()) }
        scoped { SyncFavoriteAlbumsUseCase(get(), get(), get()) }
        scoped { RemoveLocalNonFavoriteSongsUseCase(get(),get(),get()) }
        scoped { MainUseCases(get(), get(), get(), get(), get()) }


        scoped { ExoPlayerManager(context = get<Application>(), get()) }

        viewModel { MusicScreenViewModel(get(), get(), get(), get()) }

        viewModel { ArtistScreenViewModel(get(), get(), get(), get()) }

        viewModel { MainScreenViewModel(get(), get(), get(), get(), get(), get()) }

        viewModel { FavoriteScreenViewModel(get(), get(), get(), get()) }

        viewModel { PlaylistScreenViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get()) }

        viewModel { SelectSongsViewModel(get(), get()) }

        viewModel{ CreatePlaylistViewModel(get(), get(), get(), get()) }

        viewModel { CreateSongViewModel(get(), get(), get(), get(), get(), get()) }

        viewModel { CreateArtistViewModel(get(), get(), get(), get()) }

        viewModel { SearchScreenViewModel(get(), get(), get(), get(), get(), get()) }
    }
}