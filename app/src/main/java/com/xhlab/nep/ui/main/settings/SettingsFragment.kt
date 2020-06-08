package com.xhlab.nep.ui.main.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.lifecycle.observe
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xhlab.nep.R
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.service.IconUnzipService
import com.xhlab.nep.ui.dialogs.IconUnzipDialog
import com.xhlab.nep.util.updateGlobalTheme
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.AndroidSupportInjection
import org.jetbrains.anko.support.v4.longToast
import javax.inject.Inject

class SettingsFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: SettingsViewModel

    private var dbLoaded: Preference? = null
    private var iconLoaded: SwitchPreferenceCompat? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference, rootKey)
        initPreference()
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)

        viewModel.currentThemeObservable.observe(this) {
            updateGlobalTheme(it)
        }

        viewModel.isDBLoaded.observe(this) { isLoaded ->
            dbLoaded?.setSummaryProvider {
                getString(when (isLoaded) {
                    true -> R.string.txt_loaded
                    false -> R.string.txt_empty
                })
            }
        }

        viewModel.isIconLoaded.observe(this) { isLoaded ->
            iconLoaded?.isChecked = isLoaded
            iconLoaded?.setSummaryProvider {
                getString(when {
                    isLoaded -> R.string.txt_loaded
                    !isLoaded && !isExternalDirectoryEmpty() -> R.string.txt_unloaded
                    else -> R.string.txt_empty
                })
            }
        }
    }

    private fun initPreference() {
        dbLoaded = findPreference(getString(R.string.key_db_status_ui))
        iconLoaded = findPreference(getString(R.string.key_icon_status))
        iconLoaded?.setOnPreferenceChangeListener { _, newValue ->
            val result = if (newValue == true) {
                if (isExternalDirectoryEmpty()) {
                    browseZipFiles()
                } else {
                    showIconLoadDialog()
                }
                false
            } else true
            result
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                val uri = data?.data
                if (uri != null) {
                    when (requestCode) {
                        READ_ZIP_CODE -> showUnzipNoticeDialog(uri)
                    }
                } else {
                    longToast(R.string.txt_invalid_uri)
                }
            }
            Activity.RESULT_CANCELED -> Unit
            else -> {
                longToast(R.string.error_open_file_failed)
            }
        }
    }

    private fun browseZipFiles() {
        browseFiles("application/zip", READ_ZIP_CODE)
    }

    private fun browseFiles(type: String, code: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            this.type = type
            addCategory(Intent.CATEGORY_OPENABLE)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        startActivityForResult(intent, code)
    }

    private fun showUnzipNoticeDialog(fileUri: Uri) {

        fun showIconUnzipDialog(fileUri: Uri) {
            IconUnzipDialog().apply {
                arguments = Bundle().apply { putParcelable(IconUnzipService.ZIP_URI, fileUri) }
            }.show(requireFragmentManager(), IconUnzipDialog.SHOW_ICON_UNZIP_DIALOG)
        }

        showNoticeDialog(R.string.title_unzip_icon, R.string.txt_unzip_icon) {
            showIconUnzipDialog(fileUri)
        }
    }

    private fun showNoticeDialog(
        @StringRes titleId: Int,
        @StringRes messageId: Int,
        positiveListener: () -> Unit
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(titleId)
            .setMessage(messageId)
            .setPositiveButton(R.string.btn_ok) { _, _ -> positiveListener.invoke() }
            .setNegativeButton(R.string.btn_cancel, null)
            .show()
    }

    private fun showIconLoadDialog() {
        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.title_load_new_icons)
            .setMessage(R.string.txt_load_new_icons)
            .setPositiveButton(R.string.btn_load) { _, _ -> browseZipFiles() }
            .setNegativeButton(R.string.btn_cancel, null)
            .setNeutralButton(R.string.btn_use_existing) { _, _ -> viewModel.setIconLoaded(true) }
            .show()
    }

    private fun isExternalDirectoryEmpty(): Boolean {
        return requireContext().getExternalFilesDir("icons")?.list()?.isEmpty() == true
    }

    companion object {
        private const val READ_ZIP_CODE = 101
    }
}