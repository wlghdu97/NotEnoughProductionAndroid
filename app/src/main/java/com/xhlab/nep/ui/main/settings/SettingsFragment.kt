package com.xhlab.nep.ui.main.settings

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.observe
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.xhlab.nep.R
import com.xhlab.nep.di.ViewModelFactory
import com.xhlab.nep.util.updateGlobalTheme
import com.xhlab.nep.util.viewModelProvider
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class SettingsFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: SettingsViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
        initViewModel()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference, rootKey)
    }

    private fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)

        viewModel.currentThemeObservable.observe(this) {
            updateGlobalTheme(it)
        }

        viewModel.isDBLoaded.observe(this) { isLoaded ->
            val preference = findPreference<Preference>(getString(R.string.key_db_status_ui))
            preference?.setSummaryProvider {
                getString(when (isLoaded) {
                    true -> R.string.txt_db_loaded
                    false -> R.string.txt_db_empty
                })
            }
        }
    }
}