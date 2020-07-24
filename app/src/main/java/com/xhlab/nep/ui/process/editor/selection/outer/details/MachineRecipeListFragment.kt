package com.xhlab.nep.ui.process.editor.selection.outer.details

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.isGone
import androidx.lifecycle.observe
import com.xhlab.nep.R
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.process.editor.selection.outer.RecipeSelectionViewModel
import com.xhlab.nep.ui.util.LinearItemSpacingDecorator
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_machine_recipe_list.*
import org.jetbrains.anko.dip
import javax.inject.Inject

class MachineRecipeListFragment : DaggerFragment(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var recipeSelectionViewModel: RecipeSelectionViewModel
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
                selectionListener = recipeSelectionViewModel,
                oreDictSelectionListener = recipeSelectionViewModel
            )
            adapter = recipeAdapter
            addItemDecoration(LinearItemSpacingDecorator(dip(4)))
        }
    }

    override fun initViewModel() {
        recipeSelectionViewModel = requireActivity().viewModelProvider(viewModelFactory)
        viewModel = viewModelProvider(viewModelFactory)
        val elementId = arguments?.getLong(ELEMENT_ID)
        val machineId = arguments?.getInt(MACHINE_ID)
        val connectToParent = arguments?.getBoolean(CONNECT_TO_PARENT)
        viewModel.init(elementId, machineId, connectToParent)

        recipeSelectionViewModel.constraint.observe(this) {
            recipeAdapter.setConnectionConstraint(it)
        }

        viewModel.recipeList.observe(this) {
            recipeAdapter.submitList(it)
            empty_text.isGone = it?.isEmpty() != true
        }

        viewModel.usageList.observe(this) {
            recipeAdapter.submitList(it)
            empty_text.isGone = it?.isEmpty() != true
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
        const val CONNECT_TO_PARENT = "connect_to_parent"
    }
}
