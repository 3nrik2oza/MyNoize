package com.project.mynoize.activities.main.domain

import com.project.mynoize.core.domain.Error

enum class RestoreSessionError: Error {
    PlaylistNotFound,
    SongsNotFound,
    LocalDataUnavailable,
    Unknown
}