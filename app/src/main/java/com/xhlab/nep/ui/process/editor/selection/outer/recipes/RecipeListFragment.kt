package com.xhlab.nep.ui.process.editor.selection.outer.recipes

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.lifecycle.observe
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xhlab.nep.R
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.model.ElementView
import com.xhlab.nep.shared.util.isSuccessful
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.element.recipes.RecipeMachineAdapter
import com.xhlab.nep.ui.process.editor.selection.outer.RecipeSelectionActivity.Companion.MACHINE_RECIPE_LIST_TAG
import com.xhlab.nep.ui.process.editor.selection.outer.RecipeSelectionViewModel
import com.xhlab.nep.ui.process.editor.selection.outer.details.MachineRecipeListFragment
import com.xhlab.nep.ui.process.editor.selection.outer.details.MachineRecipeListFragment.Companion.MACHINE_ID
import com.xhlab.nep.util.observeNotNull
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_recipe_list.*
import org.jetbrains.anko.support.v4.longToast
import javax.inject.Inject

class RecipeListFragment : DaggerFragment(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var recipeSelectionViewModel: RecipeSelectionViewModel
    private lateinit var viewModel: RecipeListViewModel

    private val recipeAdapter by lazy { RecipeMachineAdapter(viewModel) }

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
        setHasOptionsMenu(true)
        recipe_list.adapter = recipeAdapter
    }

    override fun initViewModel() {
        recipeSelectionViewModel = requireActivity().viewModelProvider(viewModelFactory)
        viewModel = viewModelProvider(viewModelFactory)

        recipeSelectionViewModel.constraint.observe(this) {
            val elementKey = arguments?.getString(ELEMENT_KEY)
            val elementId = arguments?.getLong(ELEMENT_ID)
            if (elementKey != null) {
                viewModel.init(elementKey, it)
            } else {
                viewModel.init(elementId, it)
            }
        }

        viewModel.elements.observeNotNull(this) {
            showElementSelectionDialog(it)
        }

        viewModel.element.observeNotNull(this) {
            val subtitle = when (it.localizedName.isEmpty()) {
                true -> it.unlocalizedName
                false -> it.localizedName
            }
            (activity as? AppCompatActivity)?.supportActionBar?.subtitle = subtitle
        }

        viewModel.recipeList.observe(this) {
            recipeAdapter.submitList(it)
            empty_text.isGone = it?.isEmpty() != true
        }

        viewModel.usageList.observe(this) {
            recipeAdapter.submitList(it)
            empty_text.isGone = it?.isEmpty() != true
        }

        viewModel.navigateToDetails.observe(this) { (elementId, machineId, connectToParent) ->
            navigateToDetails(elementId, machineId, connectToParent)
        }

        viewModel.modificationResult.observe(this) {
            if (it.isSuccessful()) {
                activity?.finish()
            } else if (it.throwable != null) {
                longToast(R.string.error_failed_to_modify_process)
            }
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

    private fun showElementSelectionDialog(elementList: List<ElementView>) {
        val nameArray = elementList.map { it.localizedName }.toTypedArray()
        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.title_select_element)
            .setItems(nameArray) { _, index -> viewModel.submitElement(elementList[index]) }
            .setNegativeButton(R.string.btn_cancel) { _, _ -> activity?.finish() }
            .setCancelable(false)
            .show()
    }

    private fun navigateToDetails(elementId: Long, machineId: Int, connectToParent: Boolean) {
        val fragment = MachineRecipeListFragment().apply {
            arguments = Bundle().apply {
                putLong(ELEMENT_ID, elementId)
                putInt(MACHINE_ID, machineId)
                putBoolean(CONNECT_TO_PARENT, connectToParent)
            }
        }
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment, MACHINE_RECIPE_LIST_TAG)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        const val ELEMENT_ID = "element_id"
        const val ELEMENT_KEY = "element_key"
        const val CONNECT_TO_PARENT = "connect_to_parent"
    }
}