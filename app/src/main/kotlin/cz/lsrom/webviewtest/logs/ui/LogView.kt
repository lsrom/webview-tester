/*
 * Copyright © 2019, Oracle and/or its affiliates. All rights reserved.
 */

package cz.lsrom.webviewtest.logs.ui

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import cz.lsrom.webviewtest.R
import cz.lsrom.webviewtest.logs.platform.LogPresenter
import cz.lsrom.webviewtest.tabs.ui.BaseTabView
import kotlinx.android.synthetic.main.log_view.*
import timber.log.Timber

internal class LogView : BaseTabView() {

    private lateinit var presenter: LogPresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.log_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = init()
    }

    override fun onResume() {
        super.onResume()
        logs.text = presenter.getLogsAsText()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.log_view_menu, menu)

        (menu.findItem(R.id.log_view_menu_search).actionView as SearchView).let { searchView ->
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    hideKeyboard()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let { logs.text = presenter.filterLogsToText(it) }
                    return true
                }
            })

            searchView.setOnCloseListener {
                logs.text = presenter.getLogsAsText()
                false
            }
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.log_view_menu_share -> presenter.shareLogs(requireContext())
        }
        return super.onOptionsItemSelected(item)
    }

    override fun tabTitle(context: Context) = context.getString(R.string.tab_logs_title)
}
