package com.project.mynoize.core.domain

interface FbError: Error {

    enum class Auth : FbError {
        INVALID_EMAIL,
        EMAIL_ALREADY_IN_USE,
        INVALID_PASSWORD,
        USER_NOT_FOUND,
        USER_DISABLED,
        ERROR_TOO_MANY_REQUESTS,
        ERROR_NETWORK_REQUEST_FAILED,
        UNKNOWN
    }

    enum class Storage : FbError {
        OBJECT_NOT_FOUND,
        BUCKET_NOT_FOUND,
        QUOTA_EXCEEDED,
        NOT_AUTHENTICATED,
        NOT_AUTHORIZED,
        UNKNOWN
    }

    enum class Firestore : FbError {
        PERMISSION_DENIED,
        UNAVAILABLE,
        ABORTED,
        NOT_FOUND,
        ALREADY_EXISTS,
        DEADLINE_EXCEEDED,
        CANCELLED,
        UNKNOWN
    }

}