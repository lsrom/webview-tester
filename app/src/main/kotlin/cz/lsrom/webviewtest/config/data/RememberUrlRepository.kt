package cz.lsrom.webviewtest.config.data

import android.content.Context
import android.content.Context.MODE_PRIVATE

private const val PREFERENCES = "storage"
private const val REMEMBER_URL = "remember_url"
private const val REMEMBER_URL_VALUE = "remember_url_value"

internal object RememberUrlRepository {

    fun setRememberUrl(value: Boolean, context: Context){
        context
            .getSharedPreferences(PREFERENCES, MODE_PRIVATE)
            .edit()
            .putBoolean(REMEMBER_URL, value)
            .apply()
    }

    fun isRememberUrl(context: Context): Boolean {
        return context
            .getSharedPreferences(PREFERENCES, MODE_PRIVATE)
            .getBoolean(REMEMBER_URL, false)
    }

    fun setUrl(value: String, context: Context){
        context
            .getSharedPreferences(PREFERENCES, MODE_PRIVATE)
            .edit()
            .putString(REMEMBER_URL_VALUE, value)
            .apply()
    }

    fun getUrl(context: Context): String? {
        return context
            .getSharedPreferences(PREFERENCES, MODE_PRIVATE)
            .getString(REMEMBER_URL_VALUE, null)
    }
}
