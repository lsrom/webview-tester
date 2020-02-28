package cz.lsrom.webviewtest.webview.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.*
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.core.view.isVisible
import cz.lsrom.webviewtest.R
import cz.lsrom.webviewtest.config.platform.ConfigPresenter
import cz.lsrom.webviewtest.logs.platform.LogPresenter
import cz.lsrom.webviewtest.tabs.ui.BaseTabView
import cz.lsrom.webviewtest.webview.platform.WebViewPresenter
import kotlinx.android.synthetic.main.webview_view.*
import timber.log.Timber

internal class WebViewView : BaseTabView() {

    private lateinit var presenter: WebViewPresenter
    private lateinit var logPresenter: LogPresenter
    private lateinit var configPresenter: ConfigPresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        presenter = init()
        logPresenter = init()
        configPresenter = init()
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.webview_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(webview) {
            setupWebView()
            setChromeClient()
            setWebViewClient()
            presenter.getUrl().also {
                Timber.d("Loading url: $it")
                loadUrl(it)
            }
            configPresenter.currentUserAgent(webview.settings.userAgentString)
        }
    }

    override fun onResume() {
        super.onResume()
        if (webview.url.isNullOrBlank() || webview.url == "about:blank"){
            webview.isVisible = false
            empty.isVisible = true
        } else {
            webview.isVisible = true
            empty.isVisible = false

            presenter.setCustomUserAgentIfAvailable(webview, requireContext())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.webview_view_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.webview_view_menu_refresh){
            webview.loadUrl(presenter.getUrl())
        }
        return super.onOptionsItemSelected(item)
    }

    override fun tabTitle(context: Context) = context.getString(R.string.tab_webview_title)

    @SuppressLint("SetJavaScriptEnabled")
    private fun WebView.setupWebView() = presenter.setupWebView(this)

    private fun WebView.setChromeClient() {
        presenter.setChromeClient(this, logPresenter)
    }

    private fun WebView.setWebViewClient() {
        presenter.setWebViewClient(this, logPresenter, context, loading)
    }
}
