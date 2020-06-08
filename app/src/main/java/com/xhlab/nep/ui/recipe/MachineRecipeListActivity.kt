package com.xhlab.nep.ui.recipe

import android.os.Bundle
import androidx.lifecycle.observe
import com.xhlab.nep.R
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.util.LinearItemSpacingDecorator
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_machine_recipe_list.*
import org.jetbrains.anko.dip
import javax.inject.Inject

class MachineRecipeListActivity : DaggerAppCompatActivity(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: MachineRecipeListViewModel

    private lateinit var recipeAdapter: RecipeDetailAdapter

    private var elementId: Long = 0L
    private var machineId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkIntent()
        initViewModel()
        initView()
    }

    private fun checkIntent() {
        elementId = intent.getLongExtra(ELEMENT_ID, 0L)
        machineId = intent.getIntExtra(MACHINE_ID, -1)
        require(elementId != 0L)
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
        viewModel.init(elementId, machineId)

        viewModel.recipeList.observe(this) {
            recipeAdapter.submitList(it)
        }

        viewModel.isIconLoaded.observe(this) { isLoaded ->
            recipeAdapter.setIconVisibility(isLoaded)
        }
    }

    override fun initView() {
        setContentView(R.layout.activity_machine_recipe_list)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.title_recipe_list)

        with (recipe_list) {
            recipeAdapter = RecipeDetailAdapter(elementId, viewModel)
            adapter = recipeAdapter
            addItemDecoration(LinearItemSpacingDecorator(dip(4)))
        }
        recipe_list.adapter = recipeAdapter
    }

    companion object {
        const val ELEMENT_ID = "element_id"
        const val MACHINE_ID = "machine_id"
    }
}