package com.xhlab.nep.ui.process.editor

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xhlab.nep.R
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.util.observeNotNull
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_process_edit.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import javax.inject.Inject

class ProcessEditActivity :
    DaggerAppCompatActivity(),
    RecipeTreeAdapter.ProcessTreeListener,
    ViewInit
{
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: ProcessEditViewModel

    private val processTreeAdapter by lazy { RecipeTreeAdapter(this, viewModel) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_process_edit)
        initViewModel()
        initView()
    }

    override fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.title_process_editor)

        with(process_tree) {
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

        viewModel.process.observeNotNull(this) {
            supportActionBar?.subtitle = it.name
            processTreeAdapter.submitProcess(it)
        }

        viewModel.isIconLoaded.observe(this) {
            processTreeAdapter.setIconVisible(it)
        }

        viewModel.iconMode.observe(this) {
            processTreeAdapter.setShowConnection(it)
        }

        viewModel.showDisconnectionAlert.observe(this) {
            showDisconnectionAlert(it)
        }

        viewModel.connectRecipe.observe(this) {
            showConnectionSelectionAlert(it)
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
        process_tree.smoothScrollToPosition(position)
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
        MaterialAlertDialogBuilder(this)
            .setItems(R.array.connection_selection) { _, index ->
                when (index) {
                    0 -> viewModel.navigateToInternalRecipeSelection(constraint)
                    1 -> viewModel.navigateToRecipeSelection(constraint)
                }
            }
            .show()
    }

    companion object {
        const val PROCESS_ID = "process_id"
    }
}