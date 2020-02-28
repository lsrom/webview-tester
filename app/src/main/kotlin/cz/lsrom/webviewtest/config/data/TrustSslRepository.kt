package cz.lsrom.webviewtest.config.data

import android.content.Context
import android.content.Context.MODE_PRIVATE

private const val PREFERENCES = "storage"
private const val TRUST_SSL = "ssl"

internal object TrustSslRepository {

    fun setTrustAllSsl(value: Boolean, context: Context){
        context
            .getSharedPreferences(PREFERENCES, MODE_PRIVATE)
            .edit()
            .putBoolean(TRUST_SSL, value)
            .apply()
    }

    fun isTrustAllSsl(context: Context): Boolean {
        return context
            .getSharedPreferences(PREFERENCES, MODE_PRIVATE)
            .getBoolean(TRUST_SSL, false)
    }
}
