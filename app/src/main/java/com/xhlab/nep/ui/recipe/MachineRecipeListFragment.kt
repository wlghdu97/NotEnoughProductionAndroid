package com.xhlab.nep.ui.recipe

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.xhlab.nep.R
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.domain.MachineRecipeListNavigationUseCase
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.adapters.RecipeDetailAdapter
import com.xhlab.nep.ui.element.ElementDetailFragment
import com.xhlab.nep.ui.main.items.ItemBrowserFragment
import com.xhlab.nep.ui.util.LinearItemSpacingDecorator
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_machine_recipe_list.*
import org.jetbrains.anko.support.v4.dip
import javax.inject.Inject

class MachineRecipeListFragment : DaggerFragment(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: MachineRecipeListViewModel

    private lateinit var recipeAdapter: RecipeDetailAdapter

    private var elementId: Long = 0L
    private var machineId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_machine_recipe_list, container, false)
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
                if (parent is ItemBrowserFragment) {
                    parent.childFragmentManager.beginTransaction()
                        .replace(R.id.container, ElementDetailFragment.getFragment(it))
                        .commit()
                    return@observe
                }
            }
            viewModel.navigateToElementDetail(it)
        }
    }

    override fun initView() {
        with (recipe_list) {
            recipeAdapter = RecipeDetailAdapter(elementId, viewModel)
            adapter = recipeAdapter
            addItemDecoration(LinearItemSpacingDecorator(dip(4)))
        }
        recipe_list.adapter = recipeAdapter
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