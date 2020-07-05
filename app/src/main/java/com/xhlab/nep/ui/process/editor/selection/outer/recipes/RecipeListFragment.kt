package com.xhlab.nep.ui.process.editor.selection.outer.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.observe
import com.xhlab.nep.R
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.element.recipes.RecipeMachineAdapter
import com.xhlab.nep.ui.process.editor.selection.outer.RecipeSelectionActivity.Companion.CONNECT_TO_PARENT
import com.xhlab.nep.ui.process.editor.selection.outer.RecipeSelectionViewModel
import com.xhlab.nep.util.observeNotNull
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_recipe_list.*
import javax.inject.Inject

class RecipeListFragment : DaggerFragment(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var recipeSelectionViewModel: RecipeSelectionViewModel
    private lateinit var viewModel: RecipeListViewModel

    private val recipeAdapter by lazy { RecipeMachineAdapter(recipeSelectionViewModel) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recipe_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewModel()
        initView()
    }

    override fun initView() {
        recipe_list.adapter = recipeAdapter
    }

    override fun initViewModel() {
        recipeSelectionViewModel = requireActivity().viewModelProvider(viewModelFactory)
        viewModel = viewModelProvider(viewModelFactory)

        recipeSelectionViewModel.element.observeNotNull(this) {
            viewModel.init(it.id, arguments?.getBoolean(CONNECT_TO_PARENT))
        }

        viewModel.recipeList.observe(this) {
            recipeAdapter.submitList(it)
        }

        viewModel.usageList.observe(this) {
            recipeAdapter.submitList(it)
        }
    }
}