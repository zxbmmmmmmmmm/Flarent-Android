package com.bettafish.flarent.config

import androidx.annotation.StringRes
import com.bettafish.flarent.App
import com.bettafish.flarent.R

object ForumConfig {
    val baseUrl: String
        get() = string(R.string.flarum_base_url)

    val checkUpdateUrl: String
        get() = string(R.string.check_update_url)

    val userAgreementUrl: String
        get() = string(R.string.user_agreement_url)

    val privacyPolicyUrl: String
        get() = string(R.string.privacy_policy_url)

    private fun string(@StringRes id: Int): String = App.INSTANCE.getString(id)
}
