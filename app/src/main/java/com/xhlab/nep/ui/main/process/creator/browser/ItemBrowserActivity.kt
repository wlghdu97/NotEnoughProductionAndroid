package com.xhlab.nep.ui.main.process.creator.browser

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.observe
import com.xhlab.nep.R
import com.xhlab.nep.databinding.ActivityItemBrowserBinding
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.main.items.ItemBrowserFragment
import com.xhlab.nep.ui.main.process.creator.ProcessCreationDialog.Companion.KEY_ELEMENT
import com.xhlab.nep.ui.main.process.creator.ProcessCreationDialog.Companion.TARGET_RECIPE
import com.xhlab.nep.ui.main.process.creator.browser.details.MachineRecipeListFragment
import com.xhlab.nep.ui.main.process.creator.browser.recipes.RecipeListFragment
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class ItemBrowserActivity : DaggerAppCompatActivity(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: ActivityItemBrowserBinding
    private lateinit var viewModel: ProcessItemBrowserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        initView()
    }

    override fun initView() {
        binding = ActivityItemBrowserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setTitle(R.string.title_select_target_element)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, ItemBrowserFragment(viewModel))
            .commit()
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)

        viewModel.navigateToMachineList.asLiveData().observe(this) {
            switchToRecipeList(it)
        }

        viewModel.navigateToRecipeDetails.asLiveData().observe(this) { (elementId, machineId) ->
            switchToRecipeDetail(elementId, machineId)
        }

        viewModel.returnResult.asLiveData().observe(this) { (targetRecipe, keyElement) ->
            val intent = Intent().apply {
                putExtra(TARGET_RECIPE, targetRecipe)
                putExtra(KEY_ELEMENT, keyElement)
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun switchToRecipeList(elementId: Long) {
        val fragment = RecipeListFragment.getFragment(elementId)
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, 0, 0, R.anim.slide_out_left)
            .replace(R.id.container, fragment, RECIPE_LIST_TAG)
            .addToBackStack(null)
            .commit()
    }

    private fun switchToRecipeDetail(elementId: Long, machineId: Int) {
        val fragment = MachineRecipeListFragment.getFragment(elementId, machineId)
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
