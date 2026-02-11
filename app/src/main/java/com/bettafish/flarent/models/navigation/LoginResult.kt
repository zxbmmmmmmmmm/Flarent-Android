package com.bettafish.flarent.models.navigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginResult(
    val token: String,
    val id: String
) : Parcelable {
}