package com.xhlab.nep.ui.process.calculator.cycles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.observe
import com.xhlab.nep.R
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.shared.util.isSuccessful
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.process.calculator.ProcessCalculationViewModel
import com.xhlab.nep.util.observeNotNull
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_processing_order.*
import javax.inject.Inject

class ProcessingOrderFragment : DaggerFragment(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var calculationViewModel: ProcessCalculationViewModel
    private lateinit var viewModel: ProcessingOrderViewModel
    private val adapter by lazy { ProcessingOrderAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_processing_order, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewModel()
        initView()
    }

    override fun initView() {
        processing_order.adapter = adapter
    }

    override fun initViewModel() {
        calculationViewModel = requireActivity().viewModelProvider(viewModelFactory)
        viewModel = viewModelProvider(viewModelFactory)

        calculationViewModel.process.observeNotNull(this) {
            viewModel.init(it)
            adapter.submitProcess(it)
        }

        calculationViewModel.calculationResult.observe(this) {
            if (it.isSuccessful()) {
                adapter.submitRecipeRatioList(it.data!!.recipes)
            }
        }

        viewModel.subProcessList.observeNotNull(this) {
            adapter.submitSubProcessList(it)
        }
    }
}