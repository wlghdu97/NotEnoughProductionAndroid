package com.xhlab.nep.ui.process.editor.selection.outer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import com.xhlab.nep.R
import com.xhlab.nep.databinding.ActivityRecipeSelectionBinding
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.model.Element
import com.xhlab.nep.shared.model.defaultJson
import com.xhlab.nep.shared.ui.process.editor.ProcessEditViewModel
import com.xhlab.nep.shared.ui.process.editor.selection.outer.RecipeSelectionViewModel
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.process.editor.selection.outer.recipes.RecipeListFragment
import com.xhlab.nep.ui.process.editor.selection.outer.replacements.OreDictListFragment
import com.xhlab.nep.ui.process.editor.selection.outer.replacements.ReplacementListFragment
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import javax.inject.Inject

class RecipeSelectionActivity : DaggerAppCompatActivity(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: ActivityRecipeSelectionBinding
    private lateinit var viewModel: RecipeSelectionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        initView()
    }

    override fun initView() {
        binding = ActivityRecipeSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setTitle(R.string.title_select_recipe_to_connect)
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
        val constraint = intent?.getStringExtra(CONSTRAINT)?.let {
            defaultJson.decodeFromString<ProcessEditViewModel.ConnectionConstraint>(it)
        }
        viewModel.init(constraint)

        viewModel.constraint.asLiveData().observe(this) {
            val elementKey = it.element.unlocalizedName
            when (it.element.type) {
                Element.ORE_CHAIN -> {
                    val replacements = it.element.unlocalizedName.split(',')
                    if (replacements.size > 1) {
                        switchToOreDictListFragment()
                    } else {
                        showReplacementListFragment(elementKey)
                    }
                }
                else -> switchToRecipeListFragment(elementKey)
            }
        }

        viewModel.connectionErrorMessage.asLiveData().observe(this) {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
        }

        viewModel.finish.asLiveData().observe(this) {
            finish()
        }
    }

    private fun switchToRecipeListFragment(elementKey: String) {
        val recipeListFragment = RecipeListFragment.getFragment(elementKey)
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, 0, 0, R.anim.slide_out_left)
            .replace(R.id.container, recipeListFragment, RECIPE_LIST_TAG)
            .commit()
    }

    private fun switchToOreDictListFragment() {
        val oreDictListFragment = OreDictListFragment()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, 0, 0, R.anim.slide_out_left)
            .replace(R.id.container, oreDictListFragment, ORE_DICT_LIST_TAG)
            .commit()
    }

    private fun showReplacementListFragment(elementKey: String) {
        val replacementListFragment = ReplacementListFragment.getFragment(elementKey)
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, 0, 0, R.anim.slide_out_left)
            .replace(R.id.container, replacementListFragment, REPLACEMENT_LIST_TAG)
            .commit()
    }

    companion object {
        const val RECIPE_LIST_TAG = "recipe_list_tag"
        const val ORE_DICT_LIST_TAG = "ore_dict_list_tag"
        const val REPLACEMENT_LIST_TAG = "replacement_list_tag"
        const val MACHINE_RECIPE_LIST_TAG = "machine_recipe_list_tag"

        const val CONSTRAINT = "constraint"

        fun Context.navigateToRecipeSelectionActivity(constraint: ProcessEditViewModel.ConnectionConstraint) {
            startActivity(Intent(this, RecipeSelectionActivity::class.java).apply {
                putExtra(CONSTRAINT, defaultJson.encodeToString(constraint))
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }
}
