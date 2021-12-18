package com.xhlab.nep.ui.process.calculator.ingredients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.asLiveData
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xhlab.nep.R
import com.xhlab.nep.databinding.FragmentBaseIngredientsBinding
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.model.RecipeElement
import com.xhlab.nep.shared.ui.process.calculator.ElementNavigatorViewModel
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.element.ElementDetailActivity.Companion.navigateToElementDetailActivity
import com.xhlab.nep.ui.process.calculator.ProcessCalculationViewModel
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class BaseIngredientsFragment : DaggerFragment(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: FragmentBaseIngredientsBinding
    private lateinit var calculationViewModel: ProcessCalculationViewModel
    private lateinit var viewModel: ElementNavigatorViewModel
    private val adapter by lazy { BaseIngredientAdapter(viewModel) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBaseIngredientsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewModel()
        initView()
    }

    override fun initView() {
        binding.baseIngredientsList.adapter = adapter
    }

    override fun initViewModel() {
        calculationViewModel = requireActivity().viewModelProvider(viewModelFactory)
        viewModel = viewModelProvider(viewModelFactory)

        calculationViewModel.isIconLoaded.asLiveData().observe(this) {
            adapter.setIconVisibility(it)
        }

        calculationViewModel.calculationResult.asLiveData().observe(this) {
            adapter.submitList(it.suppliers)
        }

        viewModel.elements.asLiveData().observe(this) {
            showElementSelectionDialog(it)
        }

        viewModel.navigateElementToDetail.asLiveData().observe(this) { elementId ->
            context?.navigateToElementDetailActivity(elementId)
        }
    }

    private fun showElementSelectionDialog(elementList: List<RecipeElement>) {
        val nameArray = elementList.map { it.localizedName }.toTypedArray()
        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.title_select_element)
            .setItems(nameArray) { _, index -> viewModel.submitElement(elementList[index]) }
            .setNegativeButton(R.string.btn_cancel) { _, _ -> activity?.finish() }
            .setCancelable(false)
            .show()
    }
}
