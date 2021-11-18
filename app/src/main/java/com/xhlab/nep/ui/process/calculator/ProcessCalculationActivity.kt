package com.xhlab.nep.ui.process.calculator

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.asLiveData
import androidx.lifecycle.observe
import androidx.viewpager.widget.ViewPager
import com.xhlab.nep.R
import com.xhlab.nep.databinding.ActivityProcessCalculationBinding
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.process.calculator.byproducts.ByproductsFragment
import com.xhlab.nep.ui.process.calculator.cycles.ProcessingOrderFragment
import com.xhlab.nep.ui.process.calculator.ingredients.BaseIngredientsFragment
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class ProcessCalculationActivity : DaggerAppCompatActivity(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: ActivityProcessCalculationBinding
    private lateinit var viewModel: ProcessCalculationViewModel
    private lateinit var pagerAdapter: ProcessCalculationViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        initView()
    }

    override fun initView() {
        binding = ActivityProcessCalculationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setTitle(R.string.title_calculation_result)

        pagerAdapter = ProcessCalculationViewPagerAdapter(supportFragmentManager)
        with(binding.viewPager) {
            offscreenPageLimit = 2
            adapter = pagerAdapter
        }

        binding.viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                binding.bottomNavigation.menu.getItem(position).isChecked = true
            }
        })

        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_process_cycle_list -> {
                    binding.viewPager.currentItem = 0
                    true
                }
                R.id.menu_base_ingredients -> {
                    binding.viewPager.currentItem = 1
                    true
                }
                R.id.menu_byproducts -> {
                    binding.viewPager.currentItem = 2
                    true
                }
                else -> false
            }
        }
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
        viewModel.init(intent?.getStringExtra(PROCESS_ID))

        viewModel.process.asLiveData().observe(this) {
            binding.toolbarLayout.toolbar.subtitle = it.name
        }

        viewModel.isResultValid.asLiveData().observe(this) {
            with(binding.message) {
                isGone = it
                if (!isGone) {
                    text = getString(R.string.error_failed_to_find_solution)
                }
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

        fun Context.navigateToProcessCalculationActivity(processId: String) {
            startActivity(Intent(this, ProcessCalculationActivity::class.java).apply {
                putExtra(PROCESS_ID, processId)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }
}
