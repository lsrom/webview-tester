package cz.lsrom.webviewtest.config.data

import android.content.Context
import android.content.Context.MODE_PRIVATE

private const val PREFERENCES = "storage"
private const val USER_AGENT = "user_agent"
private const val OVERWRITE_DEFAULT = "overwrite_default"

internal object CustomUserAgentRepository {

    fun setUserAgent(value: String, context: Context){
        context
            .getSharedPreferences(PREFERENCES, MODE_PRIVATE)
            .edit()
            .putString(USER_AGENT, value)
            .apply()
    }

    fun getUserAgent(context: Context): String? {
        return context
            .getSharedPreferences(PREFERENCES, MODE_PRIVATE)
            .getString(USER_AGENT, null)
    }

    fun removeUserAgent(context: Context) {
        context
            .getSharedPreferences(PREFERENCES, MODE_PRIVATE)
            .edit()
            .remove(USER_AGENT)
            .putBoolean(OVERWRITE_DEFAULT, false)
            .apply()
    }

    fun overwriteDefault(value: Boolean, context: Context){
        context
            .getSharedPreferences(PREFERENCES, MODE_PRIVATE)
            .edit()
            .putBoolean(OVERWRITE_DEFAULT, value)
            .apply()
    }

    fun shouldOverwriteDefault(context: Context): Boolean {
        return context
            .getSharedPreferences(PREFERENCES, MODE_PRIVATE)
            .getBoolean(OVERWRITE_DEFAULT, false)
    }
}
