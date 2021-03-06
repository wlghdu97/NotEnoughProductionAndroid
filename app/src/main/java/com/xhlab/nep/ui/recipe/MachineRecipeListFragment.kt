package com.xhlab.nep.ui.recipe

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.xhlab.nep.R
import com.xhlab.nep.databinding.FragmentMachineRecipeListBinding
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.domain.MachineRecipeListNavigationUseCase
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.adapters.RecipeDetailAdapter
import com.xhlab.nep.ui.element.ElementDetailFragment
import com.xhlab.nep.ui.util.LinearItemSpacingDecorator
import com.xhlab.nep.util.dip
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class MachineRecipeListFragment : DaggerFragment(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: FragmentMachineRecipeListBinding
    private lateinit var viewModel: MachineRecipeListViewModel

    private lateinit var recipeAdapter: RecipeDetailAdapter

    private var elementId: Long = 0L
    private var machineId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMachineRecipeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        checkIntent()
        initViewModel()
        initView()
    }

    private fun checkIntent() {
        elementId = arguments?.getLong(ELEMENT_ID) ?: 0L
        machineId = arguments?.getInt(MACHINE_ID) ?: -1
        require(elementId != 0L)
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
        viewModel.init(elementId, machineId)

        viewModel.recipeList.observe(this) {
            recipeAdapter.submitList(it)
        }

        viewModel.isIconLoaded.observe(this) { isLoaded ->
            recipeAdapter.setIconVisibility(isLoaded)
        }

        viewModel.navigateToDetail.observe(this) {
            if (resources.getBoolean(R.bool.isTablet)) {
                val parent = requireParentFragment()
                parent.childFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_top, 0, 0, R.anim.slide_out_bottom)
                    .add(R.id.container, ElementDetailFragment.getFragment(it))
                    .addToBackStack(null)
                    .commit()
            } else {
                viewModel.navigateToElementDetail(it)
            }
        }
    }

    override fun initView() {
        binding.recipeListToolbar?.let {
            (activity as? AppCompatActivity)?.let { activity ->
                activity.setSupportActionBar(it)
                activity.supportActionBar?.let { actionBar ->
                    setHasOptionsMenu(true)
                    actionBar.setDisplayHomeAsUpEnabled(true)
                }
            }
        }

        with(binding.recipeList) {
            recipeAdapter = RecipeDetailAdapter(elementId, viewModel)
            adapter = recipeAdapter
            addItemDecoration(LinearItemSpacingDecorator(context.dip(4)))
        }
        binding.recipeList.adapter = recipeAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.recipe_search, menu)
        val searchMenu = menu.findItem(R.id.menu_search)
        with (searchMenu.actionView as SearchView) {
            queryHint = getString(R.string.hint_search_ingredient)
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    viewModel.searchIngredients(query ?: "")
                    return true
                }
                override fun onQueryTextChange(newText: String?) = false
            })
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            requireFragmentManager().popBackStack()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val ELEMENT_ID = "element_id"
        const val MACHINE_ID = "machine_id"

        fun getBundle(params: MachineRecipeListNavigationUseCase.Parameters): Bundle {
            return Bundle().apply {
                putLong(ELEMENT_ID, params.elementId)
                putInt(MACHINE_ID, params.machineId ?: -1)
            }
        }

        fun getFragment(params: MachineRecipeListNavigationUseCase.Parameters): Fragment {
            return MachineRecipeListFragment().apply { arguments = getBundle(params) }
        }
    }
}