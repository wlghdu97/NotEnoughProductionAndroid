package com.xhlab.nep.ui.main.process.creator.browser

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.observe
import com.xhlab.nep.R
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.main.items.ItemBrowserFragment
import com.xhlab.nep.ui.main.process.creator.ProcessCreationDialog.Companion.KEY_ELEMENT
import com.xhlab.nep.ui.main.process.creator.ProcessCreationDialog.Companion.TARGET_RECIPE
import com.xhlab.nep.ui.main.process.creator.browser.details.MachineRecipeListFragment
import com.xhlab.nep.ui.main.process.creator.browser.recipes.RecipeListFragment
import com.xhlab.nep.ui.main.process.creator.browser.recipes.RecipeListFragment.Companion.ELEMENT_ID
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.layout_toolbar.*
import javax.inject.Inject

class ItemBrowserActivity : DaggerAppCompatActivity(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: ProcessItemBrowserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_browser)
        initViewModel()
        initView()
    }

    override fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.title_select_target_element)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, ItemBrowserFragment(viewModel))
            .commit()
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)

        viewModel.navigateToMachineList.observe(this) {
            navigateToRecipeList(it)
        }

        viewModel.navigateToRecipeDetails.observe(this) { (elementId, machineId) ->
            navigateToRecipeDetail(elementId, machineId)
        }

        viewModel.returnResult.observe(this) { (targetRecipe, keyElement) ->
            val intent = Intent().apply {
                putExtra(TARGET_RECIPE, targetRecipe)
                putExtra(KEY_ELEMENT, keyElement)
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun navigateToRecipeList(elementId: Long) {
        val fragment = RecipeListFragment().apply {
            arguments = Bundle().apply { putLong(ELEMENT_ID, elementId) }
        }
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, 0, 0, R.anim.slide_out_left)
            .replace(R.id.container, fragment, RECIPE_LIST_TAG)
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToRecipeDetail(elementId: Long, machineId: Int) {
        val fragment = MachineRecipeListFragment().apply {
            arguments = Bundle().apply {
                putLong(MachineRecipeListFragment.ELEMENT_ID, elementId)
                putInt(MachineRecipeListFragment.MACHINE_ID, machineId)
            }
        }
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, 0, 0, R.anim.slide_out_left)
            .replace(R.id.container, fragment, MACHINE_RECIPE_LIST_TAG)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        private const val RECIPE_LIST_TAG = "recipe_list_tag"
        private const val MACHINE_RECIPE_LIST_TAG = "machine_recipe_list_tag"
    }
}