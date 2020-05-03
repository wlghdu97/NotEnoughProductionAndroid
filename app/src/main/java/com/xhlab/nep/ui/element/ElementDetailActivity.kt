package com.xhlab.nep.ui.element

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.xhlab.nep.R
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.shared.db.entity.ElementEntity.Companion.ORE_CHAIN
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.element.recipes.RecipeListFragment
import com.xhlab.nep.ui.element.replacements.ReplacementContainerFragment
import com.xhlab.nep.ui.element.usages.UsageListFragment
import com.xhlab.nep.util.observeNotNull
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_element_detail.*
import javax.inject.Inject

class ElementDetailActivity : DaggerAppCompatActivity(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: ElementDetailViewModel

    private lateinit var viewPagerAdapter: FragmentStatePagerAdapter

    private var elementId: Long = 0L
    private var elementType: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkIntent()
        initViewModel()
        initView()
    }

    private fun checkIntent() {
        elementId = intent.getLongExtra(ELEMENT_ID, 0L)
        elementType = intent.getIntExtra(ELEMENT_TYPE, -1)
        require(elementId != 0L)
        require(elementType != -1)
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
        viewModel.init(elementId)

        viewModel.element.observeNotNull(this) { element ->
            supportActionBar?.let {
                it.title = when (element.localizedName.isEmpty()) {
                    true -> getString(R.string.txt_unnamed)
                    false -> element.localizedName.trim()
                }
                it.subtitle = element.unlocalizedName.trim()
            }
        }
    }

    override fun initView() {
        setContentView(R.layout.activity_element_detail)
        setSupportActionBar(toolbar)

        val firstTab = tab_layout.getTabAt(0)
        if (firstTab != null) {
            firstTab.text = getString(when (elementType) {
                ORE_CHAIN -> R.string.tab_replacements
                else -> R.string.tab_recipes
            })
        }

        with (view_pager) {
            viewPagerAdapter = when (elementType) {
                ORE_CHAIN -> ReplacementListPagerAdapter(supportFragmentManager)
                else -> RecipeListPagerAdapter(supportFragmentManager)
            }
            adapter = viewPagerAdapter
            addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
                override fun onPageSelected(position: Int) {
                    tab_layout.selectTab(tab_layout.getTabAt(position))
                }
            })
        }

        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                view_pager.currentItem = tab.position
            }

            override fun onTabReselected(tab: TabLayout.Tab) = Unit
            override fun onTabUnselected(tab: TabLayout.Tab) = Unit
        })
    }

    private inner class RecipeListPagerAdapter(
        fragmentManager: FragmentManager
    ) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        private val recipeListFragment by lazy { RecipeListFragment() }
        private val usageListFragment by lazy { UsageListFragment() }

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> recipeListFragment
                1 -> usageListFragment
                else -> Fragment()
            }.apply { arguments = Bundle().apply { putLong(ELEMENT_ID, elementId) } }
        }

        override fun getCount() = 2
    }

    private inner class ReplacementListPagerAdapter(
        fragmentManager: FragmentManager
    ) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        private val replacementContainerFragment by lazy { ReplacementContainerFragment() }
        private val usageListFragment by lazy { UsageListFragment() }

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> replacementContainerFragment
                1 -> usageListFragment
                else -> Fragment()
            }.apply { arguments = Bundle().apply { putLong(ELEMENT_ID, elementId) } }
        }

        override fun getCount() = 2
    }

    companion object {
        const val ELEMENT_ID = "element_id"
        const val ELEMENT_TYPE = "element_type"
    }
}