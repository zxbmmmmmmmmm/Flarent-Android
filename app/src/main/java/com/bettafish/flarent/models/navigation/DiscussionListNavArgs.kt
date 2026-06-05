package com.bettafish.flarent.models.navigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer

@Parcelize
@Serializable
data class DiscussionListNavArgs(
    val title: String? = null,
    val filter: Array<String>? = null,
    val sort: String = "-createdAt"
) : Parcelable {

}