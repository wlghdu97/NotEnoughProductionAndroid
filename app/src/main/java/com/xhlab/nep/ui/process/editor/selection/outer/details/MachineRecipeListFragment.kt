package com.xhlab.nep.ui.process.editor.selection.outer.details

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.paging.LoadState
import com.xhlab.nep.R
import com.xhlab.nep.databinding.FragmentMachineRecipeListBinding
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.shared.ui.process.editor.selection.outer.RecipeSelectionViewModel
import com.xhlab.nep.shared.ui.process.editor.selection.outer.details.MachineRecipeListViewModel
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.process.editor.selection.outer.recipes.RecipeListFragment
import com.xhlab.nep.ui.util.LinearItemSpacingDecorator
import com.xhlab.nep.util.dip
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class MachineRecipeListFragment : DaggerFragment(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: FragmentMachineRecipeListBinding
    private lateinit var recipeSelectionViewModel: RecipeSelectionViewModel
    private lateinit var viewModel: MachineRecipeListViewModel
    private lateinit var recipeAdapter: RecipeSelectionAdapter

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
        initViewModel()
        initView()
    }

    override fun initView() {
        setHasOptionsMenu(true)
        with(binding.recipeList) {
            recipeAdapter = RecipeSelectionAdapter(
                targetElementId = arguments?.getLong(ELEMENT_ID),
                selectionListener = recipeSelectionViewModel,
                oreDictSelectionListener = recipeSelectionViewModel
            )
            adapter = recipeAdapter
            addItemDecoration(LinearItemSpacingDecorator(context.dip(4)))
        }

        recipeAdapter.addLoadStateListener {
            if (it.refresh is LoadState.NotLoading) {
                binding.emptyText.isGone = recipeAdapter.itemCount > 0
            }
        }
    }

    override fun initViewModel() {
        recipeSelectionViewModel = requireActivity().viewModelProvider(viewModelFactory)
        viewModel = viewModelProvider(viewModelFactory)
        val elementId = arguments?.getLong(ELEMENT_ID)
        val machineId = arguments?.getInt(MACHINE_ID)
        val connectToParent = arguments?.getBoolean(CONNECT_TO_PARENT)
        viewModel.init(elementId, machineId, connectToParent)

        recipeSelectionViewModel.constraint.asLiveData().observe(this) {
            recipeAdapter.setConnectionConstraint(it)
        }

        viewModel.recipeList.flatMapLatest {
            it.pagingData
        }.asLiveData().observe(this) {
            recipeAdapter.submitData(lifecycle, it)
        }

        viewModel.usageList.flatMapLatest {
            it.pagingData
        }.asLiveData().observe(this) {
            recipeAdapter.submitData(lifecycle, it)
        }

        viewModel.isIconLoaded.asLiveData().observe(this) { isLoaded ->
            recipeAdapter.setIconVisibility(isLoaded)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.recipe_search, menu)
        val searchMenu = menu.findItem(R.id.menu_search)
        with(searchMenu.actionView as SearchView) {
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

        fun getFragment(elementId: Long, machineId: Int, connectToParent: Boolean): Fragment {
            return MachineRecipeListFragment().apply {
                arguments = Bundle().apply {
                    putLong(RecipeListFragment.ELEMENT_ID, elementId)
                    putInt(MACHINE_ID, machineId)
                    putBoolean(RecipeListFragment.CONNECT_TO_PARENT, connectToParent)
                }
            }
        }
    }
}
