package com.xhlab.nep.ui.process.editor.selection.outer

import android.os.Bundle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import com.xhlab.nep.R
import com.xhlab.nep.databinding.ActivityRecipeSelectionBinding
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.shared.db.entity.ElementEntity.Companion.ORE_CHAIN
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.process.editor.ProcessEditViewModel
import com.xhlab.nep.ui.process.editor.selection.outer.recipes.RecipeListFragment
import com.xhlab.nep.ui.process.editor.selection.outer.replacements.OreDictListFragment
import com.xhlab.nep.ui.process.editor.selection.outer.replacements.ReplacementListFragment
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerAppCompatActivity
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
        viewModel.init(intent?.getSerializableExtra(CONSTRAINT) as? ProcessEditViewModel.ConnectionConstraint)

        viewModel.constraint.asLiveData().observe(this) {
            val elementKey = it.element.unlocalizedName
            when (it.element.type) {
                ORE_CHAIN -> {
                    val replacements = it.element.unlocalizedName.split(',')
                    if (replacements.size > 1) {
                        showOreDictListFragment()
                    } else {
                        showReplacementListFragment(elementKey)
                    }
                }
                else -> showRecipeListFragment(elementKey)
            }
        }

        viewModel.connectionErrorMessage.asLiveData().observe(this) {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
        }

        viewModel.finish.asLiveData().observe(this) {
            finish()
        }
    }

    private fun showRecipeListFragment(elementKey: String) {
        val recipeListFragment = RecipeListFragment().apply {
            arguments = Bundle().apply { putString(RecipeListFragment.ELEMENT_KEY, elementKey) }
        }
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, 0, 0, R.anim.slide_out_left)
            .replace(R.id.container, recipeListFragment, RECIPE_LIST_TAG)
            .commit()
    }

    private fun showOreDictListFragment() {
        val oreDictListFragment = OreDictListFragment()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, 0, 0, R.anim.slide_out_left)
            .replace(R.id.container, oreDictListFragment, ORE_DICT_LIST_TAG)
            .commit()
    }

    private fun showReplacementListFragment(elementKey: String) {
        val replacementListFragment = ReplacementListFragment().apply {
            arguments =
                Bundle().apply { putString(ReplacementListFragment.ELEMENT_KEY, elementKey) }
        }
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
    }
}
