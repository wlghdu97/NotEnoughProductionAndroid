package com.xhlab.nep.ui.process.editor

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.xhlab.nep.R
import com.xhlab.nep.databinding.ActivityProcessEditBinding
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.shared.ui.process.editor.ProcessEditViewModel
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.process.calculator.ProcessCalculationActivity.Companion.navigateToProcessCalculationActivity
import com.xhlab.nep.ui.process.editor.selection.internal.InternalRecipeSelectionActivity.Companion.navigateToInternalRecipeSelectionActivity
import com.xhlab.nep.ui.process.editor.selection.outer.RecipeSelectionActivity.Companion.navigateToRecipeSelectionActivity
import com.xhlab.nep.ui.process.editor.selection.subprocess.ProcessSelectionActivity.Companion.navigateToProcessSelectionActivity
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class ProcessEditActivity :
    DaggerAppCompatActivity(),
    RecipeTreeAdapter.ProcessTreeListener,
    ViewInit {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: ActivityProcessEditBinding
    private lateinit var viewModel: ProcessEditViewModel

    private val processTreeAdapter by lazy { RecipeTreeAdapter(this, viewModel) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        initView()
    }

    override fun initView() {
        binding = ActivityProcessEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.setTitle(R.string.title_process_editor)

        with(binding.processTree) {
            adapter = processTreeAdapter
            layoutManager = object : LinearLayoutManager(context) {
                override fun smoothScrollToPosition(
                    recyclerView: RecyclerView,
                    state: RecyclerView.State,
                    position: Int
                ) {
                    super.smoothScrollToPosition(recyclerView, state, position)
                    val smoothScroller: LinearSmoothScroller =
                        object : LinearSmoothScroller(recyclerView.context) {
                            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                                return 80f / displayMetrics.densityDpi
                            }
                        }
                    smoothScroller.targetPosition = position
                    startSmoothScroll(smoothScroller)
                }
            }
        }
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
        viewModel.init(intent?.getStringExtra(PROCESS_ID))

        viewModel.process.asLiveData().observe(this) {
            supportActionBar?.subtitle = it.name
            processTreeAdapter.submitProcess(it)
        }

        viewModel.isIconLoaded.asLiveData().observe(this) {
            processTreeAdapter.setIconVisible(it)
        }

        viewModel.iconMode.asLiveData().observe(this) {
            processTreeAdapter.setShowConnection(it)
        }

        viewModel.showDisconnectionAlert.asLiveData().observe(this) {
            showDisconnectionAlert(it)
        }

        viewModel.connectRecipe.asLiveData().observe(this) {
            showConnectionSelectionAlert(it)
        }

        viewModel.modificationErrorMessage.asLiveData().observe(this) {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
        }

        viewModel.navigateToInternalRecipeSelection.asLiveData().observe(this) {
            navigateToInternalRecipeSelectionActivity(it)
        }

        viewModel.navigateToRecipeSelection.asLiveData().observe(this) {
            navigateToRecipeSelectionActivity(it)
        }

        viewModel.navigateToProcessSelection.asLiveData().observe(this) {
            navigateToProcessSelectionActivity(it)
        }

        viewModel.navigateToCalculation.asLiveData().observe(this) {
            navigateToProcessCalculationActivity(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.process_edit, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_swap_icons -> {
                viewModel.toggleIconMode()
            }
            R.id.menu_calculate -> {
                viewModel.navigateToCalculation()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onProcessTreeExpanded(position: Int) {
        binding.processTree.smoothScrollToPosition(position)
    }

    @SuppressLint("InflateParams")
    private fun showDisconnectionAlert(payload: ProcessEditViewModel.DisconnectionPayload) {
        val view = layoutInflater.inflate(R.layout.layout_disconnection_alert, null)
        val checkbox = view.findViewById<CheckBox>(R.id.checkbox)
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.title_disconnect_recipe)
            .setMessage(R.string.txt_disconnect_recipe)
            .setView(view)
            .setPositiveButton(R.string.btn_ok) { _, _ ->
                viewModel.disconnect(payload, checkbox.isChecked)
            }
            .setNegativeButton(R.string.btn_cancel, null)
            .show()
    }

    private fun showConnectionSelectionAlert(constraint: ProcessEditViewModel.ConnectionConstraint) {
        val arrayId = when (constraint.connectToParent) {
            true -> R.array.connection_selection
            false -> R.array.connection_selection_child
        }
        MaterialAlertDialogBuilder(this)
            .setItems(arrayId) { _, index ->
                when (index) {
                    0 -> viewModel.navigateToInternalRecipeSelection(constraint)
                    1 -> viewModel.navigateToRecipeSelection(constraint)
                    2 -> viewModel.attachSupplier(
                        constraint.recipe,
                        constraint.element.unlocalizedName
                    )
                    3 -> viewModel.navigateToProcessSelection(constraint)
                }
            }
            .show()
    }

    companion object {
        const val PROCESS_ID = "process_id"

        fun Context.navigateToProcessEditActivity(processId: String) {
            startActivity(Intent(this, ProcessEditActivity::class.java).apply {
                putExtra(PROCESS_ID, processId)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }
}
