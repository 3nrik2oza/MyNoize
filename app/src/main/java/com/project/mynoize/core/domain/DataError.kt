package com.project.mynoize.core.domain

sealed interface DataError: Error {
    enum class Remote : DataError {
        REQUEST_TIMEOUT,
        TOO_MANY_REQUEST,
        NO_INTERNET,
        SERVER,
        SERIALIZATION,
        UNKNOWN
    }
}