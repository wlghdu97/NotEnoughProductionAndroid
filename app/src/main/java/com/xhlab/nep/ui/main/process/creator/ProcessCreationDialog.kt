package com.xhlab.nep.ui.main.process.creator

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.observe
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.xhlab.nep.R
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.recipes.view.CraftingRecipeView
import com.xhlab.nep.model.recipes.view.MachineRecipeView
import com.xhlab.nep.ui.ViewInit
import com.xhlab.nep.ui.main.process.creator.browser.ItemBrowserActivity
import com.xhlab.nep.util.getIcon
import com.xhlab.nep.util.observeNotNull
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.DaggerDialogFragment
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.support.v4.longToast
import javax.inject.Inject

class ProcessCreationDialog : DaggerDialogFragment(), ViewInit {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: ProcessCreationViewModel
    private lateinit var dialogView: View

    private lateinit var nameInputLayout: TextInputLayout
    private lateinit var nameEdit: EditText
    private lateinit var recipeButton: MaterialButton

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireContext().layoutInflater
        dialogView = inflater.inflate(R.layout.dialog_process_creation, null)

        return MaterialAlertDialogBuilder(context)
            .setTitle(R.string.title_create_process)
            .setView(dialogView)
            .setPositiveButton(R.string.btn_create) { _, _  -> viewModel.createProcess()}
            .setNegativeButton(R.string.btn_cancel, null)
            .create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewModel()
        initView()
    }

    override fun initView() {
        nameInputLayout = dialogView.findViewById(R.id.name_input_layout)
        nameEdit = dialogView.findViewById(R.id.name_edit)
        recipeButton = dialogView.findViewById(R.id.btn_target_recipe)

        with (nameEdit) {
            addTextChangedListener { viewModel.changeProcessName(it.toString()) }
            requestFocus()
        }

        recipeButton.setOnClickListener {
            startItemBrowserForResult()
        }
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)

        viewModel.processName.observeNotNull(this) {
            if (nameEdit.text.toString() != it) {
                nameEdit.setText(it)
            }
        }

        viewModel.recipePair.observe(this) { (targetRecipe, keyElement) ->
            val machineName = when (targetRecipe) {
                is MachineRecipeView -> targetRecipe.machineName
                is CraftingRecipeView -> getString(R.string.txt_crafting_table)
                else -> getString(R.string.txt_unknown)
            }
            recipeButton.icon = requireContext().getIcon(keyElement.unlocalizedName)
            recipeButton.text = String.format(
                getString(R.string.form_recipe_details),
                machineName,
                keyElement.amount,
                keyElement.localizedName
            )
        }

        viewModel.isNameValid.observeNotNull(this) {
            nameInputLayout.helperText = when (it) {
                true -> ""
                false -> getString(R.string.txt_name_empty)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_RECIPE_ID_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val targetRecipe = data.getSerializableExtra(TARGET_RECIPE) as? Recipe
                val keyElement = data.getSerializableExtra(KEY_ELEMENT) as? Element
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