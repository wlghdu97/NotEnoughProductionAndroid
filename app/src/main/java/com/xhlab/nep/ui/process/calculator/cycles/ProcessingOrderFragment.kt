package com.xhlab.nep.ui.process.calculator.cycles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.asLiveData
import androidx.lifecycle.observe
import com.xhlab.nep.databinding.FragmentProcessingOrderBinding
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.process.calculator.ProcessCalculationViewModel
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class ProcessingOrderFragment : DaggerFragment(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: FragmentProcessingOrderBinding
    private lateinit var calculationViewModel: ProcessCalculationViewModel
    private val adapter by lazy { ProcessingOrderAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProcessingOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewModel()
        initView()
    }

    override fun initView() {
        binding.processingOrder.adapter = adapter
    }

    override fun initViewModel() {
        calculationViewModel = requireActivity().viewModelProvider(viewModelFactory)

        calculationViewModel.process.asLiveData().observe(this) {
            adapter.submitProcess(it)
        }

        calculationViewModel.calculationResult.asLiveData().observe(this) {
            adapter.submitRecipeRatioList(it.recipes)
        }
    }
}
