package com.xhlab.nep.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.xhlab.nep.R
import com.xhlab.nep.databinding.ActivityMainBinding
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.main.items.ItemBrowserFragment
import com.xhlab.nep.ui.main.machines.MachineBrowserFragment
import com.xhlab.nep.ui.main.process.ProcessListFragment
import com.xhlab.nep.ui.main.settings.SettingsContainerFragment
import com.xhlab.nep.util.updateGlobalTheme
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: ActivityMainBinding
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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)

        pagerAdapter = MainViewPagerAdapter(supportFragmentManager)
        with (binding.viewPager) {
            offscreenPageLimit = 3
            adapter = pagerAdapter
        }

        binding.viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                binding.bottomNavigation.menu.getItem(position).isChecked = true
            }
        })

        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_item_browser -> {
                    binding.viewPager.currentItem = 0
                    true
                }
                R.id.menu_machine_browser -> {
                    binding.viewPager.currentItem = 1
                    true
                }
                R.id.menu_process -> {
                    binding.viewPager.currentItem = 2
                    true
                }
                R.id.menu_settings -> {
                    binding.viewPager.currentItem = 3
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
        private val processFragment by lazy { ProcessListFragment() }
        private val settingsContainerFragment by lazy { SettingsContainerFragment() }

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> itemBrowserFragment
                1 -> machineBrowserFragment
                2 -> processFragment
                3 -> settingsContainerFragment
                else -> throw IllegalArgumentException("invalid position.")
            }
        }

        override fun getCount() = 4
    }
}
