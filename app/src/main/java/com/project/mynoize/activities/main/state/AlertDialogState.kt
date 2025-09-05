package com.project.mynoize.activities.main.state

import com.project.mynoize.core.presentation.UiText

data class AlertDialogState (
    val show: Boolean = false,
    val loading: Boolean = false,
    val message: UiText? = null
    )