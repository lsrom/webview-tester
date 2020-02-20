/*
 * Copyright © 2019, Oracle and/or its affiliates. All rights reserved.
 */

package cz.lsrom.webviewtest.webview.platform

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.http.SslError
import android.view.View
import android.webkit.*
import androidx.lifecycle.ViewModel
import cz.lsrom.webviewtest.config.data.CustomUserAgentRepository
import cz.lsrom.webviewtest.config.data.TrustSslRepository
import cz.lsrom.webviewtest.logs.platform.LogPresenter
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

internal class WebViewPresenter : ViewModel() {

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS", Locale.getDefault())

    private var url = ""

    fun setUrl(value: String) {
        url = value
    }

    fun getUrl() = url

    fun setCustomUserAgentIfAvailable(webView: WebView, context: Context){
        CustomUserAgentRepository
            .getUserAgent(context)
            ?.let { customUserAgent -> webView.settings.userAgentString = customUserAgent }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun setupWebView(webView: WebView) = with(webView.settings) {
        javaScriptEnabled = true
        javaScriptCanOpenWindowsAutomatically = true
        allowContentAccess = true
        supportMultipleWindows()
        allowFileAccessFromFileURLs = true
        allowUniversalAccessFromFileURLs = true
        WebView.setWebContentsDebuggingEnabled(true)
        domStorageEnabled = true
        databaseEnabled = true
    }

    fun setChromeClient(webView: WebView, logPresenter: LogPresenter) {
        webView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                consoleMessage?.let { message ->
                    logPresenter.addLog(formatLogMessage(message))
                }

                return super.onConsoleMessage(consoleMessage)
            }
        }
    }

    fun setWebViewClient(
        webView: WebView,
        logPresenter: LogPresenter,
        context: Context,
        loading: View
    ) {
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                Timber.d("Received error.")
            }

            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler,
                error: SslError
            ) {
                logPresenter.addLog("SSL error: ${error.url}: ${error}")
                if (TrustSslRepository.isTrustAllSsl(context)) {
                    handler.proceed()
                    logPresenter.addLog("!!!Proceeding to load the page despite insecure content!!!")
                } else {
                    handler.cancel()
                    logPresenter.addLog("Not loading insecure content. You can change this behavior by flipping the switch above.")
                }
            }

            override fun onReceivedHttpError(
                view: WebView?,
                request: WebResourceRequest?,
                errorResponse: WebResourceResponse
            ) {
                super.onReceivedHttpError(view, request, errorResponse)
                Timber.d("HTTP error: ${errorResponse.reasonPhrase}")
            }

            override fun onPageStarted(
                view: WebView?,
                url: String,
                favicon: Bitmap?
            ) {
                super.onPageStarted(view, url, favicon)
                Timber.d("onPageStarted: $url")
                showProgressDialog(loading, url)
            }

            override fun onPageFinished(view: WebView?, url: String) {
                super.onPageFinished(view, url)
                Timber.d("onPageFinished: $url")
                hideProgressDialog(loading)
            }

            override fun onLoadResource(view: WebView?, url: String) {
                super.onLoadResource(view, url)
                Timber.d("onLoadResource: $url")
            }

            override fun onPageCommitVisible(view: WebView?, url: String?) {
                super.onPageCommitVisible(view, url)
                Timber.d("Loading finished?")
            }
        }
    }

    private fun formatLogMessage(consoleMessage: ConsoleMessage): String {
        return with(consoleMessage) {
            "${now()} [${lineNumber()}] ${messageLevel().name} : ${message()}"
        }
    }

    private fun now() = dateFormatter.format(Date())

    private fun showProgressDialog(view: View, url: String) = Unit
    private fun hideProgressDialog(view: View) = Unit
}
