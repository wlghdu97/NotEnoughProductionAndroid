package com.xhlab.nep.ui.main.process.creator

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.asLiveData
import androidx.lifecycle.observe
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xhlab.nep.R
import com.xhlab.nep.databinding.DialogProcessCreationBinding
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.RecipeElement
import com.xhlab.nep.model.recipes.view.CraftingRecipeView
import com.xhlab.nep.model.recipes.view.MachineRecipeView
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.main.process.creator.browser.ItemBrowserActivity
import com.xhlab.nep.util.getIcon
import com.xhlab.nep.util.longToast
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerDialogFragment
import javax.inject.Inject

class ProcessCreationDialog : DaggerDialogFragment(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: DialogProcessCreationBinding
    private lateinit var viewModel: ProcessCreationViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogProcessCreationBinding.inflate(LayoutInflater.from(context))

        return MaterialAlertDialogBuilder(context)
            .setTitle(R.string.title_create_process)
            .setView(binding.root)
            .setPositiveButton(R.string.btn_create, null)
            .setNegativeButton(R.string.btn_cancel, null)
            .create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewModel()
        initView()
    }

    override fun initView() {
        with(binding.nameEdit) {
            addTextChangedListener { viewModel.changeProcessName(it.toString()) }
            requestFocus()
        }

        binding.btnTargetRecipe.setOnClickListener {
            startItemBrowserForResult()
        }

        dialog?.setOnShowListener {
            val createButton = (it as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            createButton.setOnClickListener {
                viewModel.createProcess()
            }
        }
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)

        viewModel.processName.asLiveData().observe(this) {
            if (binding.nameEdit.text.toString() != it) {
                binding.nameEdit.setText(it)
            }
        }

        viewModel.recipePair.asLiveData().observe(this) { (targetRecipe, keyElement) ->
            val machineName = when (targetRecipe) {
                is MachineRecipeView -> targetRecipe.machineName
                is CraftingRecipeView -> getString(R.string.txt_crafting_table)
                else -> getString(R.string.txt_unknown)
            }
            binding.btnTargetRecipe.icon = requireContext().getIcon(keyElement.unlocalizedName)
            binding.btnTargetRecipe.text = String.format(
                getString(R.string.form_recipe_details),
                machineName,
                keyElement.amount,
                keyElement.localizedName
            )
        }

        viewModel.isNameValid.asLiveData().observe(this) {
            binding.nameInputLayout.helperText = when (it) {
                true -> ""
                false -> getString(R.string.txt_name_empty)
            }
        }

        viewModel.creationErrorMessage.asLiveData().observe(this) {
            longToast(it)
        }

        viewModel.dismiss.asLiveData().observe(this) {
            dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_RECIPE_ID_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val targetRecipe = data.getSerializableExtra(TARGET_RECIPE) as? Recipe
                val keyElement = data.getSerializableExtra(KEY_ELEMENT) as? RecipeElement
                if (targetRecipe != null && keyElement != null) {
                    viewModel.submitRecipe(targetRecipe, keyElement)
                }
            } else {
                longToast(R.string.error_failed_to_fetch_recipe)
            }
        }
    }

    private fun startItemBrowserForResult() {
        val intent = Intent(context, ItemBrowserActivity::class.java)
        startActivityForResult(intent, REQUEST_RECIPE_ID_CODE)
    }

    companion object {
        const val TARGET_RECIPE = "target_recipe"
        const val KEY_ELEMENT = "key_element"
        const val REQUEST_RECIPE_ID_CODE = 102
    }
}
