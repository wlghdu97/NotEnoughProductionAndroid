package com.xhlab.nep.ui.process.calculator.byproducts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.asLiveData
import androidx.lifecycle.observe
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xhlab.nep.R
import com.xhlab.nep.databinding.FragmentByproductsBinding
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.model.ElementView
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.process.calculator.ElementNavigatorViewModel
import com.xhlab.nep.ui.process.calculator.ProcessCalculationViewModel
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class ByproductsFragment : DaggerFragment(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: FragmentByproductsBinding
    private lateinit var calculationViewModel: ProcessCalculationViewModel
    private lateinit var viewModel: ElementNavigatorViewModel
    private val adapter by lazy { ByproductsAdapter(viewModel) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentByproductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewModel()
        initView()
    }

    override fun initView() {
        binding.byproductList.adapter = adapter
    }

    override fun initViewModel() {
        calculationViewModel = requireActivity().viewModelProvider(viewModelFactory)
        viewModel = viewModelProvider(viewModelFactory)

        calculationViewModel.isIconLoaded.asLiveData().observe(this) {
            adapter.setIconVisibility(it)
        }

        calculationViewModel.process.asLiveData().observe(this) {
            adapter.submitProcess(it)
        }

        calculationViewModel.calculationResult.asLiveData().observe(this) {
            adapter.submitRecipeRatioList(it.recipes)
        }

        viewModel.elements.asLiveData().observe(this) {
            showElementSelectionDialog(it)
        }
    }

    private fun showElementSelectionDialog(elementList: List<ElementView>) {
        val nameArray = elementList.map { it.localizedName }.toTypedArray()
        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.title_select_element)
            .setItems(nameArray) { _, index -> viewModel.submitElement(elementList[index]) }
            .setNegativeButton(R.string.btn_cancel) { _, _ -> activity?.finish() }
            .setCancelable(false)
            .show()
    }
}
