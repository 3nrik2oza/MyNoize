package com.project.mynoize.core.presentation

import com.project.mynoize.R
import com.project.mynoize.core.domain.DataError
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