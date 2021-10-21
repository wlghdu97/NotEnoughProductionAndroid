package com.xhlab.nep.ui.main.process.creator.browser.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.asLiveData
import androidx.lifecycle.observe
import com.xhlab.nep.databinding.FragmentRecipeListBinding
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.element.recipes.RecipeMachineAdapter
import com.xhlab.nep.ui.main.process.creator.browser.ProcessItemBrowserViewModel
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class RecipeListFragment : DaggerFragment(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: FragmentRecipeListBinding
    private lateinit var processItemBrowserViewModel: ProcessItemBrowserViewModel
    private lateinit var viewModel: RecipeListViewModel

    private val recipeAdapter by lazy { RecipeMachineAdapter(processItemBrowserViewModel) }

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
        initViewModel()
        initView()
    }

    override fun initView() {
        binding.recipeList.adapter = recipeAdapter
    }

    override fun initViewModel() {
        processItemBrowserViewModel = requireActivity().viewModelProvider(viewModelFactory)
        viewModel = viewModelProvider(viewModelFactory)
        viewModel.init(arguments?.getLong(ELEMENT_ID))

        viewModel.recipeList.flatMapLatest {
            it.pagingData
        }.asLiveData().observe(this) {
            recipeAdapter.submitData(lifecycle, it)
        }
    }

    companion object {
        const val ELEMENT_ID = "element_id"
    }
}
