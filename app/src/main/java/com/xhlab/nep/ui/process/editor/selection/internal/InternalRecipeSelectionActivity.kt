package com.xhlab.nep.ui.process.editor.selection.internal

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.isGone
import androidx.lifecycle.asLiveData
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import com.xhlab.nep.R
import com.xhlab.nep.databinding.ActivityRecipeSelectionExistingBinding
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.shared.util.isSuccessful
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.process.editor.ProcessEditViewModel
import com.xhlab.nep.util.observeNotNull
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerAppCompatActivity
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
        viewModel.init(intent?.getSerializableExtra(CONSTRAINT) as? ProcessEditViewModel.ConnectionConstraint)

        viewModel.process.observeNotNull(this) {
            processTreeAdapter.submitProcess(it)
            binding.emptyText.isGone = processTreeAdapter.itemCount != 0
        }

        viewModel.constraint.observe(this) {
            processTreeAdapter.setConnectionConstraint(it)
        }

        viewModel.isIconLoaded.asLiveData().observe(this) {
            processTreeAdapter.setIconVisible(it)
        }

        viewModel.iconMode.observe(this) {
            processTreeAdapter.setShowConnection(it)
        }

        viewModel.connectionResult.observe(this) {
            if (it.isSuccessful()) {
                finish()
            } else {
                Snackbar.make(
                    binding.root,
                    R.string.error_connect_recipe_failed,
                    Snackbar.LENGTH_LONG
                ).show()
            }
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
        const val CONSTRAINT = "constraint"
    }
}
