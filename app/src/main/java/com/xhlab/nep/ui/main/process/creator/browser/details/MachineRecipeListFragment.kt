package com.xhlab.nep.ui.main.process.creator.browser.details

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.observe
import com.xhlab.nep.R
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.main.process.creator.browser.ProcessItemBrowserViewModel
import com.xhlab.nep.ui.util.LinearItemSpacingDecorator
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_machine_recipe_list.*
import org.jetbrains.anko.support.v4.dip
import javax.inject.Inject

class MachineRecipeListFragment : DaggerFragment(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var processItemBrowserViewModel: ProcessItemBrowserViewModel
    private lateinit var viewModel: MachineRecipeListViewModel
    private lateinit var recipeAdapter: RecipeSelectionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_machine_recipe_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewModel()
        initView()
    }

    override fun initView() {
        setHasOptionsMenu(true)
        with (recipe_list) {
            recipeAdapter = RecipeSelectionAdapter(
                targetElementId = arguments?.getLong(ELEMENT_ID),
                selectionListener = processItemBrowserViewModel
            )
            adapter = recipeAdapter
            addItemDecoration(LinearItemSpacingDecorator(dip(4)))
        }
    }

    override fun initViewModel() {
        processItemBrowserViewModel = requireActivity().viewModelProvider(viewModelFactory)
        viewModel = viewModelProvider(viewModelFactory)
        val elementId = arguments?.getLong(ELEMENT_ID)
        val machineId = arguments?.getInt(MACHINE_ID)
        viewModel.init(elementId, machineId)

        viewModel.recipeList.observe(this) {
            recipeAdapter.submitList(it)
        }

        viewModel.isIconLoaded.observe(this) { isLoaded ->
            recipeAdapter.setIconVisibility(isLoaded)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.recipe_search, menu)
        val searchMenu = menu.findItem(R.id.menu_search)
        with (searchMenu.actionView as SearchView) {
            queryHint = getString(R.string.hint_search_ingredient)
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?) = false
                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.searchIngredients(newText ?: "")
                    return true
                }
            })
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    companion object {
        const val ELEMENT_ID = "element_id"
        const val MACHINE_ID = "machine_id"
    }
}