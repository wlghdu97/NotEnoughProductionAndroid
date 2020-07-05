package com.xhlab.nep.ui.process.editor.selection.outer.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.observe
import com.xhlab.nep.R
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.process.editor.selection.outer.RecipeSelectionActivity.Companion.CONNECT_TO_PARENT
import com.xhlab.nep.ui.process.editor.selection.outer.RecipeSelectionViewModel
import com.xhlab.nep.ui.util.LinearItemSpacingDecorator
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.activity_machine_recipe_list.*
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
        with (recipe_list) {
            recipeAdapter = RecipeSelectionAdapter(
                targetElementId = arguments?.getLong(ELEMENT_ID),
                selectionListener = recipeSelectionViewModel
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
        }

        viewModel.usageList.observe(this) {
            recipeAdapter.submitList(it)
        }

        viewModel.isIconLoaded.observe(this) { isLoaded ->
            recipeAdapter.setIconVisibility(isLoaded)
        }
    }

    companion object {
        const val ELEMENT_ID = "element_id"
        const val MACHINE_ID = "machine_id"
    }
}
