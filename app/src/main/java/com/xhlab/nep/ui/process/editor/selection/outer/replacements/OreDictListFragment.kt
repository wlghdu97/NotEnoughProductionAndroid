package com.xhlab.nep.ui.process.editor.selection.outer.replacements

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.observe
import com.xhlab.nep.R
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.element.replacements.OreDictAdapter
import com.xhlab.nep.ui.process.editor.selection.outer.RecipeSelectionActivity
import com.xhlab.nep.ui.process.editor.selection.outer.RecipeSelectionViewModel
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_ore_dict_list.*
import javax.inject.Inject

class OreDictListFragment : DaggerFragment(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var recipeSelectionViewModel: RecipeSelectionViewModel
    private lateinit var viewModel: OreDictListViewModel

    private val oreDictAdapter by lazy { OreDictAdapter(viewModel) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ore_dict_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
    }

    override fun initView() {
        ore_dict_list.adapter = oreDictAdapter
    }

    override fun initViewModel() {
        recipeSelectionViewModel = requireActivity().viewModelProvider(viewModelFactory)
        viewModel = viewModelProvider(viewModelFactory)

        recipeSelectionViewModel.constraint.observe(this) {
            viewModel.init(it.element.id)
        }

        viewModel.oreDictNameList.observe(this) {
            oreDictAdapter.submitList(it)
        }

        viewModel.navigateToReplacementList.observe(this) {
            switchToReplacementList(it)
        }
    }

    private fun switchToReplacementList(elementKey: String) {
        val replacementListFragment = ReplacementListFragment().apply {
            arguments = Bundle().apply { putString(ReplacementListFragment.ELEMENT_KEY, elementKey) }
        }
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, 0, 0, R.anim.slide_out_left)
            .replace(R.id.container, replacementListFragment, RecipeSelectionActivity.REPLACEMENT_LIST_TAG)
            .commit()
    }
}