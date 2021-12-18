package com.xhlab.nep.ui.process.editor.selection.outer.recipes

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.lifecycle.observe
import androidx.paging.LoadState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xhlab.nep.R
import com.xhlab.nep.databinding.FragmentRecipeListBinding
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.model.RecipeElement
import com.xhlab.nep.shared.ui.process.editor.selection.outer.RecipeSelectionViewModel
import com.xhlab.nep.shared.ui.process.editor.selection.outer.recipes.RecipeListViewModel
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.element.recipes.RecipeMachineAdapter
import com.xhlab.nep.ui.process.editor.selection.outer.RecipeSelectionActivity.Companion.MACHINE_RECIPE_LIST_TAG
import com.xhlab.nep.ui.process.editor.selection.outer.details.MachineRecipeListFragment
import com.xhlab.nep.util.longToast
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class RecipeListFragment : DaggerFragment(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: FragmentRecipeListBinding
    private lateinit var recipeSelectionViewModel: RecipeSelectionViewModel
    private lateinit var viewModel: RecipeListViewModel

    private val recipeAdapter by lazy { RecipeMachineAdapter(viewModel) }

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
        setHasOptionsMenu(true)
        binding.recipeList.adapter = recipeAdapter

        recipeAdapter.addLoadStateListener {
            if (it.refresh is LoadState.NotLoading) {
                binding.emptyText.isGone = recipeAdapter.itemCount > 0
            }
        }
    }

    override fun initViewModel() {
        recipeSelectionViewModel = requireActivity().viewModelProvider(viewModelFactory)
        viewModel = viewModelProvider(viewModelFactory)

        recipeSelectionViewModel.constraint.asLiveData().observe(this) {
            val elementKey = arguments?.getString(ELEMENT_KEY)
            val elementId = arguments?.getLong(ELEMENT_ID)
            if (elementKey != null) {
                viewModel.init(elementKey, it)
            } else {
                viewModel.init(elementId, it)
            }
        }

        viewModel.elements.asLiveData().observe(this) {
            showElementSelectionDialog(it)
        }

        viewModel.element.asLiveData().observe(this) {
            val subtitle = when (it.localizedName.isEmpty()) {
                true -> it.unlocalizedName
                false -> it.localizedName
            }
            (activity as? AppCompatActivity)?.supportActionBar?.subtitle = subtitle
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

        viewModel.navigateToDetails.asLiveData()
            .observe(this) { (elementId, machineId, connectToParent) ->
                navigateToDetails(elementId, machineId, connectToParent)
            }

        viewModel.modificationErrorMessage.asLiveData().observe(this) {
            longToast(it)
        }

        viewModel.finish.asLiveData().observe(this) {
            activity?.finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (arguments?.getLong(ELEMENT_ID) != 0L) {
            inflater.inflate(R.menu.ore_dict_list_selection, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_attach_as_supplier) {
            viewModel.attachOreDictAsSupplier()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showElementSelectionDialog(elementList: List<RecipeElement>) {
        val nameArray = elementList.map { it.localizedName }.toTypedArray()
        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.title_select_element)
            .setItems(nameArray) { _, index -> viewModel.submitElement(elementList[index]) }
            .setNegativeButton(R.string.btn_cancel) { _, _ -> activity?.finish() }
            .setCancelable(false)
            .show()
    }

    private fun navigateToDetails(elementId: Long, machineId: Int, connectToParent: Boolean) {
        val fragment = MachineRecipeListFragment.getFragment(elementId, machineId, connectToParent)
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, 0, 0, R.anim.slide_out_left)
            .replace(R.id.container, fragment, MACHINE_RECIPE_LIST_TAG)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        const val ELEMENT_ID = "element_id"
        const val ELEMENT_KEY = "element_key"
        const val CONNECT_TO_PARENT = "connect_to_parent"

        fun getFragment(elementId: Long): Fragment {
            return RecipeListFragment().apply {
                arguments = Bundle().apply { putLong(ELEMENT_ID, elementId) }
            }
        }

        fun getFragment(elementKey: String): Fragment {
            return RecipeListFragment().apply {
                arguments = Bundle().apply { putString(ELEMENT_KEY, elementKey) }
            }
        }
    }
}
