package com.xhlab.nep.ui.process.editor.selection.outer.replacements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import com.xhlab.nep.R
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.main.items.ElementDetailAdapter
import com.xhlab.nep.ui.process.editor.selection.outer.RecipeSelectionActivity
import com.xhlab.nep.ui.process.editor.selection.outer.RecipeSelectionViewModel
import com.xhlab.nep.ui.process.editor.selection.outer.recipes.RecipeListFragment
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_replacement_list_selection.*
import javax.inject.Inject

class ReplacementListFragment : DaggerFragment(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var recipeSelectionViewModel: RecipeSelectionViewModel
    private lateinit var viewModel: ReplacementListViewModel

    private val elementAdapter by lazy { ElementDetailAdapter(viewModel) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_replacement_list_selection, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewModel()
        initView()
    }

    override fun initViewModel() {
        recipeSelectionViewModel = requireActivity().viewModelProvider(viewModelFactory)
        viewModel = viewModelProvider(viewModelFactory)

        recipeSelectionViewModel.constraint.observe(this) {
            val unlocalizedName = arguments?.getString(ELEMENT_KEY)
            viewModel.init(unlocalizedName)
            (activity as? AppCompatActivity)?.supportActionBar?.subtitle = unlocalizedName
        }

        viewModel.replacementList.observe(this) {
            elementAdapter.submitList(it)
        }

        viewModel.isIconLoaded.observe(this) { isLoaded ->
            elementAdapter.setIconVisibility(isLoaded)
        }

        viewModel.navigateToRecipeList.observe(this) { elementId ->
            showRecipeListFragment(elementId)
        }

        viewModel.navigateToRecipeListWithKey.observe(this) { elementKey ->
            showRecipeListFragment(elementKey)
        }
    }

    override fun initView() {
        replacement_list.adapter = elementAdapter
    }

    private fun showRecipeListFragment(elementId: Long) {
        val recipeListFragment = RecipeListFragment().apply {
            arguments = Bundle().apply { putLong(RecipeListFragment.ELEMENT_ID, elementId) }
        }
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, 0, 0, R.anim.slide_out_left)
            .replace(R.id.container, recipeListFragment, RecipeSelectionActivity.RECIPE_LIST_TAG)
            .addToBackStack(null)
            .commit()
    }

    private fun showRecipeListFragment(elementKey: String) {
        val recipeListFragment = RecipeListFragment().apply {
            arguments = Bundle().apply { putString(RecipeListFragment.ELEMENT_KEY, elementKey) }
        }
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, 0, 0, R.anim.slide_out_left)
            .replace(R.id.container, recipeListFragment, RecipeSelectionActivity.RECIPE_LIST_TAG)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        const val ELEMENT_KEY = "element_key"
    }
}