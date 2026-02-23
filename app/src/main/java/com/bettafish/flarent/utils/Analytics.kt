package com.bettafish.flarent.utils

sealed class Analytics{
    object Event {
        const val VIEW_DISCUSSION: String = "view_discussion"

        const val VIEW_USER_PROFILE: String = "view_user_profile"
        const val EDIT_POST: String = "edit_post"
        const val REPLY: String = "reply"
    }

    object Param {
        const val CURRENT_USERNAME: String = "current_username"
        const val CURRENT_USER_ID: String = "current_user_id"

        const val USERNAME: String = "username"

        const val USER_ID: String = "user_id"

        const val DISCUSSION_ID: String = "discussion_id"
        const val DISCUSSION_TITLE: String = "discussion_title"
    }
}