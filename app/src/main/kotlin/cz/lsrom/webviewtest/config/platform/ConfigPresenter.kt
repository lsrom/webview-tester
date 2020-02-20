/*
 * Copyright © 2019, Oracle and/or its affiliates. All rights reserved.
 */

package cz.lsrom.webviewtest.config.platform

import android.content.Context
import androidx.lifecycle.ViewModel
import cz.lsrom.webviewtest.R
import cz.lsrom.webviewtest.config.data.CustomUserAgentRepository
import cz.lsrom.webviewtest.config.data.RememberUrlRepository
import cz.lsrom.webviewtest.config.data.TrustSslRepository

internal class ConfigPresenter : ViewModel() {

    private var webViewAction: () -> Unit = { Unit }
    private var currentUserAgent: String? = null

    fun currentUserAgent(value: String) {
        currentUserAgent = value
    }

    fun getUserAgent(context: Context): String {
        return CustomUserAgentRepository
            .getUserAgent(context)
            ?: currentUserAgent
            ?: context.getString(R.string.config_user_agent_empty_message)
    }

    fun overwriteDefaultUserAgent(value: Boolean, context: Context){
        CustomUserAgentRepository.overwriteDefault(value, context)
    }

    fun shouldOverwriteDefaultUserAgent(context: Context): Boolean {
        return CustomUserAgentRepository.shouldOverwriteDefault(context)
    }

    fun saveCustomUserAgent(value: String, context: Context) {
        CustomUserAgentRepository.setUserAgent(value, context)
    }

    fun resetCustomUserAgent(context: Context) {
        CustomUserAgentRepository.removeUserAgent(context)
    }

    fun setTrustAllSsl(value: Boolean, context: Context) {
        TrustSslRepository.setTrustAllSsl(value, context)
    }

    fun isTrustAllSsl(context: Context): Boolean = TrustSslRepository.isTrustAllSsl(context)

    fun setRememberUrl(value: Boolean, url: String, context: Context) {
        RememberUrlRepository.setRememberUrl(value, context)
        if (value) {
            RememberUrlRepository.setUrl(url, context)
        }
    }

    fun isRememberUrl(context: Context): Boolean = RememberUrlRepository.isRememberUrl(context)

    fun setWebViewAction(action: () -> Unit) {
        webViewAction = action
    }

    fun runWebViewAction() = webViewAction()

    fun rememberUrl(value: String, context: Context) {
        RememberUrlRepository.setUrl(value, context)
    }

    fun getDefaultUrl(context: Context): String {
        return if (RememberUrlRepository.isRememberUrl(context)) {
            RememberUrlRepository
                .getUrl(context)
                ?: context.getString(R.string.config_default_url)
        } else {
            context.getString(R.string.config_default_url)
        }
    }
}
