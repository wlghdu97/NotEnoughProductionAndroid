package com.xhlab.nep.ui.process.editor.selection.outer.replacements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import com.xhlab.nep.R
import com.xhlab.nep.databinding.FragmentReplacementListSelectionBinding
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.shared.ui.process.editor.selection.outer.RecipeSelectionViewModel
import com.xhlab.nep.shared.ui.process.editor.selection.outer.replacements.ReplacementListViewModel
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.main.items.RecipeElementDetailAdapter
import com.xhlab.nep.ui.process.editor.selection.outer.RecipeSelectionActivity
import com.xhlab.nep.ui.process.editor.selection.outer.recipes.RecipeListFragment
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class ReplacementListFragment : DaggerFragment(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: FragmentReplacementListSelectionBinding
    private lateinit var recipeSelectionViewModel: RecipeSelectionViewModel
    private lateinit var viewModel: ReplacementListViewModel

    private val elementAdapter by lazy { RecipeElementDetailAdapter(viewModel) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReplacementListSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewModel()
        initView()
    }

    override fun initViewModel() {
        recipeSelectionViewModel = requireActivity().viewModelProvider(viewModelFactory)
        viewModel = viewModelProvider(viewModelFactory)

        recipeSelectionViewModel.constraint.asLiveData().observe(this) {
            val unlocalizedName = arguments?.getString(ELEMENT_KEY)
            viewModel.init(unlocalizedName)
            (activity as? AppCompatActivity)?.supportActionBar?.subtitle = unlocalizedName
        }

        viewModel.replacementList.flatMapLatest {
            it.pagingData
        }.asLiveData().observe(this) {
            elementAdapter.submitData(lifecycle, it)
        }

        viewModel.isIconLoaded.asLiveData().observe(this) { isLoaded ->
            elementAdapter.setIconVisibility(isLoaded)
        }

        viewModel.navigateToRecipeList.asLiveData().observe(this) { elementId ->
            switchToRecipeListFragment(elementId)
        }

        viewModel.navigateToRecipeListWithKey.asLiveData().observe(this) { elementKey ->
            switchToRecipeListFragment(elementKey)
        }
    }

    override fun initView() {
        binding.replacementList.adapter = elementAdapter
    }

    private fun switchToRecipeListFragment(elementId: Long) {
        val recipeListFragment = RecipeListFragment.getFragment(elementId)
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, 0, 0, R.anim.slide_out_left)
            .replace(R.id.container, recipeListFragment, RecipeSelectionActivity.RECIPE_LIST_TAG)
            .addToBackStack(null)
            .commit()
    }

    private fun switchToRecipeListFragment(elementKey: String) {
        val recipeListFragment = RecipeListFragment.getFragment(elementKey)
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, 0, 0, R.anim.slide_out_left)
            .replace(R.id.container, recipeListFragment, RecipeSelectionActivity.RECIPE_LIST_TAG)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        const val ELEMENT_KEY = "element_key"

        fun getFragment(elementKey: String): Fragment {
            return ReplacementListFragment().apply {
                arguments = Bundle().apply { putString(ELEMENT_KEY, elementKey) }
            }
        }
    }
}
