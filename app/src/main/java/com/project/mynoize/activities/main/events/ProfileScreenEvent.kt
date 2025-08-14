package com.project.mynoize.activities.main.events

sealed class ProfileScreenEvent {
    object OnSignOutClick : ProfileScreenEvent()
}