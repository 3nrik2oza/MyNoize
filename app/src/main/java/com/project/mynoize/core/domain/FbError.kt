package com.project.mynoize.core.domain

interface FbError: Error {

    enum class Auth : FbError {
        INVALID_EMAIL,
        INVALID_PASSWORD,
        USER_NOT_FOUND,
        USER_DISABLED,
        ERROR_TOO_MANY_REQUESTS,
        ERROR_NETWORK_REQUEST_FAILED,
        UNKNOWN
    }

}