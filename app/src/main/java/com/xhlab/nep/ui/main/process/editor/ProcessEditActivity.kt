package com.xhlab.nep.ui.main.process.editor

import android.os.Bundle
import com.xhlab.nep.R
import dagger.android.support.DaggerAppCompatActivity

class ProcessEditActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_process_edit)
    }

    companion object {
        const val PROCESS_ID = "process_id"
    }
}