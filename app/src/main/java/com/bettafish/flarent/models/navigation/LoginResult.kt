package com.bettafish.flarent.models.navigation

import android.os.Parcelable
import com.bettafish.flarent.models.Tag
import com.bettafish.flarent.models.User
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginResult(
    val token: String,
    val id: String
) : Parcelable {
}