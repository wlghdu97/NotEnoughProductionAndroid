package com.xhlab.nep.ui.element

import android.content.Context
import android.content.Intent
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

    companion object {
        fun Context.navigateToElementDetailActivity(elementId: Long, elementType: Int) {
            startActivity(Intent(this, ElementDetailActivity::class.java).apply {
                putExtra(ElementDetailFragment.ELEMENT_ID, elementId)
                putExtra(ElementDetailFragment.ELEMENT_TYPE, elementType)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }
}
