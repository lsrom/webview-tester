/*
 * Copyright © 2019, Oracle and/or its affiliates. All rights reserved.
 */

package cz.lsrom.webviewtest.tabs.ui

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

internal abstract class BaseTabView: Fragment() {

    abstract fun tabTitle(context: Context): String

    protected inline fun <reified T : ViewModel> init(): T {
        return ViewModelProvider(requireActivity()).get(T::class.java)
    }
}
