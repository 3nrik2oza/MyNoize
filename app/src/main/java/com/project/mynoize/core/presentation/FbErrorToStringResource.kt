package com.project.mynoize.core.presentation

import com.project.mynoize.R
import com.project.mynoize.core.domain.FbError

fun FbError.Auth.toErrorMessage() : UiText{
    val stringRes = when(this){
        FbError.Auth.INVALID_EMAIL -> R.string.error_incorrect_email
        FbError.Auth.INVALID_PASSWORD -> R.string.error_incorrect_password
        FbError.Auth.USER_NOT_FOUND -> R.string.error_invalid_credential
        FbError.Auth.USER_DISABLED -> R.string.error_user_disabled
        FbError.Auth.ERROR_TOO_MANY_REQUESTS -> R.string.error_too_many_requests
        FbError.Auth.ERROR_NETWORK_REQUEST_FAILED -> R.string.error_network_request_failed
        FbError.Auth.EMAIL_ALREADY_IN_USE -> R.string.error_email_already_in_use
        FbError.Auth.UNKNOWN -> R.string.error_unknown
    }
    return UiText.StringResource(stringRes)
}


fun FbError.Storage.toErrorMessage() : UiText{
    val stringRes = when(this){
        FbError.Storage.OBJECT_NOT_FOUND -> R.string.error_object_not_found
        FbError.Storage.BUCKET_NOT_FOUND -> R.string.error_bucket_not_found
        FbError.Storage.QUOTA_EXCEEDED -> R.string.error_quota_exceeded
        FbError.Storage.NOT_AUTHENTICATED -> R.string.error_not_authenticated
        FbError.Storage.NOT_AUTHORIZED -> R.string.error_not_authorized
        FbError.Storage.UNKNOWN -> R.string.error_unknown
    }
    return UiText.StringResource(stringRes)
}

fun FbError.Firestore.toErrorMessage() : UiText{
    val stringRes = when(this){
        FbError.Firestore.PERMISSION_DENIED -> R.string.error_permission_denied
        FbError.Firestore.UNAVAILABLE -> R.string.error_unavailable
        FbError.Firestore.ABORTED -> R.string.error_aborted
        FbError.Firestore.NOT_FOUND -> R.string.error_not_found
        FbError.Firestore.ALREADY_EXISTS -> R.string.error_already_exists
        FbError.Firestore.DEADLINE_EXCEEDED -> R.string.error_deadline_exceeded
        FbError.Firestore.CANCELLED -> R.string.error_cancelled
        FbError.Firestore.UNKNOWN -> R.string.error_unknown
    }
    return UiText.StringResource(stringRes)
}