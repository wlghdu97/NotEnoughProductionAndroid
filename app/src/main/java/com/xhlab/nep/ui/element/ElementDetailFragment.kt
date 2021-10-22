package com.xhlab.nep.ui.element

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.asLiveData
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.xhlab.nep.R
import com.xhlab.nep.databinding.FragmentElementDetailBinding
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.domain.ElementDetailNavigationUseCase
import com.xhlab.nep.shared.db.entity.ElementEntity
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.element.recipes.RecipeListFragment
import com.xhlab.nep.ui.element.replacements.ReplacementContainerFragment
import com.xhlab.nep.ui.element.usages.UsageListFragment
import com.xhlab.nep.util.getCardBackgroundColor
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class ElementDetailFragment : DaggerFragment(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: FragmentElementDetailBinding
    private lateinit var viewModel: ElementDetailViewModel

    private lateinit var viewPagerAdapter: FragmentStatePagerAdapter

    private var elementId: Long = 0L
    private var elementType: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentElementDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        checkIntent()
        initViewModel()
        initView()
    }

    private fun checkIntent() {
        elementId = arguments?.getLong(ELEMENT_ID) ?: 0L
        elementType = arguments?.getInt(ELEMENT_TYPE) ?: -1
        require(elementId != 0L)
        require(elementType != -1)
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
        viewModel.init(elementId)

        viewModel.element.asLiveData().observe(this) { element ->
            (activity as? AppCompatActivity)?.let { activity ->
                activity.setSupportActionBar(binding.toolbar)
                activity.supportActionBar?.let {
                    if (resources.getBoolean(R.bool.isTablet)) {
                        setHasOptionsMenu(true)
                        it.setDisplayHomeAsUpEnabled(true)
                    }
                    it.title = when (element.localizedName.isEmpty()) {
                        true -> getString(R.string.txt_unnamed)
                        false -> element.localizedName.trim()
                    }
                    it.subtitle = element.unlocalizedName.trim()
                }
            }
        }
    }

    override fun initView() {
        // make background opaque when tablet mode
        binding.root.setBackgroundColor(requireContext().getCardBackgroundColor())

        val firstTab = binding.tabLayout.getTabAt(0)
        if (firstTab != null) {
            firstTab.text = getString(
                when (elementType) {
                    ElementEntity.ORE_CHAIN -> R.string.tab_replacements
                    else -> R.string.tab_recipes
                }
            )
        }

        with(binding.viewPager) {
            viewPagerAdapter = when (elementType) {
                ElementEntity.ORE_CHAIN -> ReplacementListPagerAdapter(childFragmentManager)
                else -> RecipeListPagerAdapter(childFragmentManager)
            }
            adapter = viewPagerAdapter
            addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
                override fun onPageSelected(position: Int) {
                    binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
                }
            })
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewPager.currentItem = tab.position
            }

            override fun onTabReselected(tab: TabLayout.Tab) = Unit
            override fun onTabUnselected(tab: TabLayout.Tab) = Unit
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            requireFragmentManager().popBackStack()
            return true
        }
        return super.onOptionsItemSelected(item)
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

        fun getBundle(params: ElementDetailNavigationUseCase.Parameters): Bundle {
            return Bundle().apply {
                putLong(ELEMENT_ID, params.elementId)
                putInt(ELEMENT_TYPE, params.elementType)
            }
        }

        fun getFragment(params: ElementDetailNavigationUseCase.Parameters): Fragment {
            return ElementDetailFragment().apply { arguments = getBundle(params) }
        }
    }
}
