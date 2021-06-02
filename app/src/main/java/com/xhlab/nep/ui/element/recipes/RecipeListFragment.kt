package com.xhlab.nep.ui.element.recipes

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.observe
import com.google.android.material.tabs.TabLayout
import com.xhlab.nep.R
import com.xhlab.nep.databinding.FragmentRecipeListBinding
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.element.ElementDetailFragment.Companion.ELEMENT_ID
import com.xhlab.nep.ui.recipe.MachineRecipeListFragment
import com.xhlab.nep.util.formatString
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class RecipeListFragment : DaggerFragment(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: FragmentRecipeListBinding
    private lateinit var viewModel: RecipeListViewModel

    private val recipeAdapter by lazy { RecipeMachineAdapter(viewModel) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
        viewModel.init(arguments?.getLong(ELEMENT_ID))

        // update parent tab item title
        viewModel.recipeList.observe(this) {
            val tabLayout = parentFragment?.view?.findViewById<TabLayout>(R.id.tab_layout)
            tabLayout?.getTabAt(0)?.text = requireContext().formatString(
                R.string.form_tab_recipes,
                it?.size ?: 0
            )
        }

        viewModel.recipeList.observe(this) {
            recipeAdapter.submitList(it)
        }

        viewModel.navigateToRecipeList.observe(this) {
            if (resources.getBoolean(R.bool.isTablet)) {
                val parent = requireParentFragment().requireParentFragment()
                parent.childFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_top, 0, 0, R.anim.slide_out_bottom)
                    .add(R.id.container, MachineRecipeListFragment.getFragment(it))
                    .addToBackStack(null)
                    .commit()
            } else {
                viewModel.navigateToRecipeList(it)
            }
        }
    }

    override fun initView() {
        binding.recipeList.adapter = recipeAdapter
    }
}