package com.xhlab.nep.ui.process.editor.selection.outer

import android.os.Bundle
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import com.xhlab.nep.R
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.shared.util.isSuccessful
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.process.editor.selection.outer.details.MachineRecipeListFragment
import com.xhlab.nep.ui.process.editor.selection.outer.details.MachineRecipeListFragment.Companion.ELEMENT_ID
import com.xhlab.nep.ui.process.editor.selection.outer.details.MachineRecipeListFragment.Companion.MACHINE_ID
import com.xhlab.nep.ui.process.editor.selection.outer.recipes.RecipeListFragment
import com.xhlab.nep.util.observeNotNull
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_recipe_selection_existing.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import javax.inject.Inject

// TODO: ore dict support
class RecipeSelectionActivity : DaggerAppCompatActivity(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: RecipeSelectionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_selection)
        initViewModel()
        initView()
    }

    override fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.title_select_recipe_to_connect)

        val recipeListFragment = RecipeListFragment().apply {
            arguments = Bundle().apply {
                putBoolean(
                    CONNECT_TO_PARENT,
                    intent?.getBooleanExtra(CONNECT_TO_PARENT, false) ?: false
                )
            }
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, recipeListFragment, RECIPE_LIST_TAG)
            .commit()
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
        viewModel.init(
            processId = intent?.getStringExtra(PROCESS_ID),
            connectToParent = intent?.getBooleanExtra(CONNECT_TO_PARENT, false),
            from = intent?.getSerializableExtra(RECIPE) as? Recipe,
            degree = intent?.getIntExtra(RECIPE_DEGREE, 0),
            elementKey = intent?.getStringExtra(ELEMENT_KEY)
        )

        viewModel.element.observeNotNull(this) {
            supportActionBar?.subtitle = it.localizedName
        }

        viewModel.navigateToDetails.observe(this) { (elementId, machineId) ->
            navigateToDetails(elementId, machineId)
        }

        viewModel.connectionResult.observe(this) {
            if (it.isSuccessful()) {
                finish()
            } else {
                Snackbar.make(
                    root,
                    R.string.error_connect_recipe_failed,
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun navigateToDetails(elementId: Long, machineId: Int) {
        val fragment = MachineRecipeListFragment().apply {
            arguments = Bundle().apply {
                putLong(ELEMENT_ID, elementId)
                putInt(MACHINE_ID, machineId)
                putBoolean(
                    CONNECT_TO_PARENT,
                    intent?.getBooleanExtra(CONNECT_TO_PARENT, false) ?: false
                )
            }
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment, MACHINE_RECIPE_LIST_TAG)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        private const val RECIPE_LIST_TAG = "recipe_list_tag"
        private const val MACHINE_RECIPE_LIST_TAG = "machine_recipe_list_tag"

        const val PROCESS_ID = "process_id"
        const val CONNECT_TO_PARENT = "connect_to_parent"
        const val RECIPE = "recipe"
        const val RECIPE_DEGREE = "recipe_degree"
        const val ELEMENT_KEY = "element_key"
    }
}