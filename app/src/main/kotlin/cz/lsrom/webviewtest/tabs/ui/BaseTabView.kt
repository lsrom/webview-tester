package cz.lsrom.webviewtest.tabs.ui

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

internal abstract class BaseTabView: Fragment() {

    abstract fun tabTitle(context: Context): String

    protected inline fun <reified T : ViewModel> init(): T {
        return ViewModelProvider(requireActivity()).get(T::class.java)
    }

    fun hideKeyboard() {
        requireActivity()
            .currentFocus
            ?.let {
                (requireActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(view?.windowToken, 0)
            }
    }
}
