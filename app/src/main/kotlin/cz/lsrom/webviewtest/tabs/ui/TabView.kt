package cz.lsrom.webviewtest.tabs.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cz.lsrom.webviewtest.R
import cz.lsrom.webviewtest.config.platform.ConfigPresenter
import cz.lsrom.webviewtest.config.ui.ConfigView
import cz.lsrom.webviewtest.logs.platform.LogPresenter
import cz.lsrom.webviewtest.logs.ui.LogView
import cz.lsrom.webviewtest.webview.ui.WebViewView
import kotlinx.android.synthetic.main.tab_fragment.*

internal class TabView: Fragment() {

    private lateinit var configPresenter: ConfigPresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tab_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configPresenter = init()

        view_pager.adapter = FragmentAdapter(
            views = listOf(
                ConfigView().also { configPresenter.setWebViewAction { tabs.getTabAt(2)?.select() } },
                LogView(),
                WebViewView()
            ),
            context = view.context.applicationContext,
            fragmentManager = parentFragmentManager
        )

        tabs.setupWithViewPager(view_pager)
    }

    protected inline fun <reified T : ViewModel> init(): T {
        return ViewModelProvider(requireActivity()).get(T::class.java)
    }
}
