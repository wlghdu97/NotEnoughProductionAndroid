package com.xhlab.nep.ui.process.editor.selection.internal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.isGone
import androidx.lifecycle.asLiveData
import com.google.android.material.snackbar.Snackbar
import com.xhlab.nep.R
import com.xhlab.nep.databinding.ActivityRecipeSelectionExistingBinding
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.shared.model.defaultJson
import com.xhlab.nep.shared.ui.process.editor.ProcessEditViewModel
import com.xhlab.nep.shared.ui.process.editor.selection.internal.InternalRecipeSelectionViewModel
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.process.editor.selection.outer.RecipeSelectionActivity
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import javax.inject.Inject

class InternalRecipeSelectionActivity : DaggerAppCompatActivity(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: ActivityRecipeSelectionExistingBinding
    private lateinit var viewModel: InternalRecipeSelectionViewModel

    private val processTreeAdapter by lazy { RecipeSelectionAdapter(viewModel) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        initView()
    }

    override fun initView() {
        binding = ActivityRecipeSelectionExistingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setTitle(R.string.title_select_recipe_to_connect)

        binding.recipeList.adapter = processTreeAdapter
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
        val constraint = intent?.getStringExtra(RecipeSelectionActivity.CONSTRAINT)?.let {
            defaultJson.decodeFromString<ProcessEditViewModel.ConnectionConstraint>(it)
        }
        viewModel.init(constraint)

        viewModel.process.asLiveData().observe(this) {
            processTreeAdapter.submitProcess(it)
            binding.emptyText.isGone = processTreeAdapter.itemCount != 0
        }

        viewModel.constraint.asLiveData().observe(this) {
            processTreeAdapter.setConnectionConstraint(it)
        }

        viewModel.isIconLoaded.asLiveData().observe(this) {
            processTreeAdapter.setIconVisible(it)
        }

        viewModel.iconMode.asLiveData().observe(this) {
            processTreeAdapter.setShowConnection(it)
        }

        viewModel.connectionErrorMessage.asLiveData().observe(this) {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
        }

        viewModel.finish.asLiveData().observe(this) {
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.process_internal_recipe_selection, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_swap_icons -> {
                viewModel.toggleIconMode()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val CONSTRAINT = "constraint"

        fun Context.navigateToInternalRecipeSelectionActivity(
            constraint: ProcessEditViewModel.ConnectionConstraint
        ) {
            startActivity(Intent(this, InternalRecipeSelectionActivity::class.java).apply {
                putExtra(CONSTRAINT, defaultJson.encodeToString(constraint))
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }
}
