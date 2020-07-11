package com.xhlab.nep.ui.process.editor.selection.outer

import android.os.Bundle
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import com.xhlab.nep.R
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.shared.db.entity.ElementEntity.Companion.ORE_CHAIN
import com.xhlab.nep.shared.util.isSuccessful
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.process.editor.selection.outer.recipes.RecipeListFragment
import com.xhlab.nep.ui.process.editor.selection.outer.replacements.ReplacementListFragment
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_recipe_selection_existing.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import javax.inject.Inject

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
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
        viewModel.init(
            processId = intent?.getStringExtra(PROCESS_ID),
            connectToParent = intent?.getBooleanExtra(CONNECT_TO_PARENT, false),
            from = intent?.getSerializableExtra(RECIPE) as? Recipe,
            degree = intent?.getIntExtra(RECIPE_DEGREE, 0),
            elementKey = intent?.getStringExtra(ELEMENT_KEY),
            elementType = intent?.getIntExtra(ELEMENT_TYPE, -1)
        )

        viewModel.constraint.observe(this) {
            when (it.elementType) {
                ORE_CHAIN -> showReplacementListFragment(it.elementKey)
                else -> showRecipeListFragment(it.elementKey)
            }
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

    private fun showRecipeListFragment(elementKey: String) {
        val recipeListFragment = RecipeListFragment().apply {
            arguments = Bundle().apply { putString(RecipeListFragment.ELEMENT_KEY, elementKey) }
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, recipeListFragment, RECIPE_LIST_TAG)
            .commit()
    }

    private fun showReplacementListFragment(elementKey: String) {
        val replacementListFragment = ReplacementListFragment().apply {
            arguments = Bundle().apply { putString(ReplacementListFragment.ELEMENT_KEY, elementKey) }
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, replacementListFragment, REPLACEMENT_LIST_TAG)
            .commit()
    }

    companion object {
        const val RECIPE_LIST_TAG = "recipe_list_tag"
        const val REPLACEMENT_LIST_TAG = "replacement_list_tag"
        const val MACHINE_RECIPE_LIST_TAG = "machine_recipe_list_tag"

        const val PROCESS_ID = "process_id"
        const val CONNECT_TO_PARENT = "connect_to_parent"
        const val RECIPE = "recipe"
        const val RECIPE_DEGREE = "recipe_degree"
        const val ELEMENT_KEY = "element_key"
        const val ELEMENT_TYPE = "element_type"
    }
}