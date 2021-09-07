package com.xhlab.nep.ui.element

import android.os.Bundle
import com.xhlab.nep.R
import dagger.android.support.DaggerAppCompatActivity

class ElementDetailActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_element_detail)
        val fragment = ElementDetailFragment().apply { arguments = intent.extras }
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }
}
