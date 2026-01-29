package com.bettafish.flarent.models.navigation

import android.os.Parcelable
import com.bettafish.flarent.models.Tag
import kotlinx.parcelize.Parcelize

@Parcelize
data class TagNavArgs(
    val id: String? = null,
    val name: String? = null,
    val icon: String? = null,
    val slug: String? = null,
    val description: String? = null,
    val color: String? = null,
    val discussionCount: Int? = null,
    val isChild: Boolean? = null
) : Parcelable {
    companion object {
        fun from(tag: Tag): TagNavArgs = TagNavArgs(
            id = tag.id,
            name = tag.name,
            icon = tag.icon,
            slug = tag.slug,
            description = tag.description,
            color = tag.color,
            discussionCount = tag.discussionCount,
            isChild = tag.isChild
        )
    }
}