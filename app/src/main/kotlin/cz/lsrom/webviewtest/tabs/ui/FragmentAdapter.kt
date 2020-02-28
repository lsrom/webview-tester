package cz.lsrom.webviewtest.tabs.ui

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

internal class FragmentAdapter(
    private val views: List<BaseTabView>,
    private val context: Context,
    fragmentManager: FragmentManager
): FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment = views[position]
    override fun getCount(): Int = views.size
    override fun getPageTitle(position: Int) = views[position].tabTitle(context)
}
