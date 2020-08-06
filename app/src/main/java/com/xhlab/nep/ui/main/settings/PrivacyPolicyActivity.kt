package com.xhlab.nep.ui.main.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.xhlab.nep.R
import kotlinx.android.synthetic.main.activity_privacy_policy.*
import kotlinx.android.synthetic.main.layout_toolbar.*

class PrivacyPolicyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setTitle(R.string.title_privacy_policy)
            it.setDisplayHomeAsUpEnabled(true)
        }
        web_view.loadUrl(getString(R.string.url_privacy_policy))
    }
}