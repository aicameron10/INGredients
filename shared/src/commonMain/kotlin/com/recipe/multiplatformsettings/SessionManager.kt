package com.recipe.multiplatformsettings

import com.russhwolf.settings.Settings

class SessionManager(private val settings: Settings)   {

    fun setAuthorization(value: String?) {
        if (value != null) {
            settings.putString(AUTH_TOKEN, value)
        }
    }
    fun getAuthorization(): String? {
        return settings.getStringOrNull(AUTH_TOKEN)
    }

    fun setJson(value: String?) {
        if (value != null) {
            settings.putString(JSON, value)
        }
    }
    fun getJson(): String? {
        return settings.getStringOrNull(JSON)
    }

    fun setOnBoarding(value: Boolean) {
        settings.putBoolean(ONBOARDING, value)
    }
    fun getOnBoarding(): Boolean {
        return settings.getBoolean(ONBOARDING, defaultValue = false)
    }

    companion object {
        private const val ONBOARDING= "onboarding"
        private const val AUTH_TOKEN = "auth_token"
        private const val JSON = "json"
    }
}
