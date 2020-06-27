package com.xhlab.nep.ui.process.calculator

import android.os.Bundle
import androidx.lifecycle.observe
import com.xhlab.nep.R
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.model.recipes.view.MachineRecipeView
import com.xhlab.nep.shared.util.isSuccessful
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.util.observeNotNull
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_process_calculation.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import java.text.DecimalFormat
import javax.inject.Inject

class ProcessCalculationActivity : DaggerAppCompatActivity(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: ProcessCalculationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_process_calculation)
        initViewModel()
        initView()
    }

    override fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.title_calculation_result)
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
        viewModel.init(intent?.getStringExtra(PROCESS_ID))

        viewModel.process.observeNotNull(this) {
            toolbar.subtitle = it.name
        }

        viewModel.calculationResult.observe(this) {
            result_text.text = if (it.isSuccessful()) {
                val result = it.data!!
                val format = DecimalFormat("#.##")
                val builder = StringBuilder().apply {
                    appendln("Optimal solution found.")
                    for ((recipe, ratio) in result.recipes) {
                        val machineName = when (recipe) {
                            is MachineRecipeView -> recipe.machineName
                            else -> "Crafting Table"
                        }
                        appendln("$machineName -> ${format.format(ratio)} cycle")
                    }
                    appendln("Base ingredients")
                    for ((element, ratio) in result.suppliers) {
                        appendln("${element.localizedName} <- x${format.format(ratio)}")
                    }
                }
                builder.toString()
            } else {
                "Failed to find optimal solution."
            }
        }
    }

    companion object {
        const val PROCESS_ID = "process_id"
    }
}