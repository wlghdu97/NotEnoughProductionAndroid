package com.xhlab.nep.ui.process.calculator

import android.os.Bundle
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.observe
import androidx.viewpager.widget.ViewPager
import com.xhlab.nep.R
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.process.calculator.byproducts.ByproductsFragment
import com.xhlab.nep.ui.process.calculator.cycles.ProcessingOrderFragment
import com.xhlab.nep.ui.process.calculator.ingredients.BaseIngredientsFragment
import com.xhlab.nep.util.observeNotNull
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_process_calculation.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import javax.inject.Inject

class ProcessCalculationActivity : DaggerAppCompatActivity(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: ProcessCalculationViewModel
    private lateinit var pagerAdapter: ProcessCalculationViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_process_calculation)
        initViewModel()
        initView()
    }

    override fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.title_calculation_result)

        pagerAdapter = ProcessCalculationViewPagerAdapter(supportFragmentManager)
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
                R.id.menu_process_cycle_list -> {
                    view_pager.currentItem = 0
                    true
                }
                R.id.menu_base_ingredients -> {
                    view_pager.currentItem = 1
                    true
                }
                R.id.menu_byproducts -> {
                    view_pager.currentItem = 2
                    true
                }
                else -> false
            }
        }
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
        viewModel.init(intent?.getStringExtra(PROCESS_ID))

        viewModel.process.observeNotNull(this) {
            toolbar.subtitle = it.name
        }

        viewModel.calculationResult.observe(this) {
            message.isGone = it.throwable == null
            if (!message.isGone) {
                message.text = getString(R.string.error_failed_to_find_solution)
            }
        }
    }

    private class ProcessCalculationViewPagerAdapter(
        fragmentManager: FragmentManager
    ) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        private val processingOrderFragment by lazy { ProcessingOrderFragment() }
        private val baseIngredientsFragment by lazy { BaseIngredientsFragment() }
        private val byproductsFragment by lazy { ByproductsFragment() }

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> processingOrderFragment
                1 -> baseIngredientsFragment
                2 -> byproductsFragment
                else -> throw IllegalArgumentException("invalid position.")
            }
        }

        override fun getCount() = 3
    }

    companion object {
        const val PROCESS_ID = "process_id"
    }
}