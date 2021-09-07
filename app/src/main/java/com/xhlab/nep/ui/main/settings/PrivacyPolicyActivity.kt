package com.xhlab.nep.ui.main.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.xhlab.nep.R
import com.xhlab.nep.databinding.ActivityPrivacyPolicyBinding

class PrivacyPolicyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrivacyPolicyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)
        supportActionBar?.let {
            it.setTitle(R.string.title_privacy_policy)
            it.setDisplayHomeAsUpEnabled(true)
        }
        binding.webView.loadUrl(getString(R.string.url_privacy_policy))
    }
}
