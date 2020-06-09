package com.xhlab.nep.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.xhlab.nep.R
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.main.items.ItemBrowserFragment
import com.xhlab.nep.ui.main.machines.MachineBrowserFragment
import com.xhlab.nep.ui.main.settings.SettingsFragment
import com.xhlab.nep.util.updateGlobalTheme
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: MainViewModel

    private lateinit var pagerAdapter: MainViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        initView()
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
        updateGlobalTheme(viewModel.currentTheme)
    }

    override fun initView() {
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        pagerAdapter = MainViewPagerAdapter(supportFragmentManager)
        with (view_pager) {
            offscreenPageLimit = 2
            adapter = pagerAdapter
        }

        view_pager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                bottom_navigation.menu.getItem(position).isChecked = true
            }
        })

        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_item_browser -> {
                    view_pager.currentItem = 0
                    true
                }
                R.id.menu_machine_browser -> {
                    view_pager.currentItem = 1
                    true
                }
                R.id.menu_settings -> {
                    view_pager.currentItem = 2
                    true
                }
                else -> false
            }
        }
    }

    private class MainViewPagerAdapter(
        fragmentManager: FragmentManager
    ) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        private val itemBrowserFragment by lazy { ItemBrowserFragment() }
        private val machineBrowserFragment by lazy { MachineBrowserFragment() }
        private val settingsFragment by lazy { SettingsFragment() }

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> itemBrowserFragment
                1 -> machineBrowserFragment
                2 -> settingsFragment
                else -> throw IllegalArgumentException("invalid position.")
            }
        }

        override fun getCount() = 3
    }
}
