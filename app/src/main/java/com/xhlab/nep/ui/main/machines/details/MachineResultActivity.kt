package com.xhlab.nep.ui.main.machines.details

import android.os.Bundle
import com.xhlab.nep.R
import dagger.android.support.DaggerAppCompatActivity

class MachineResultActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_machine_result)
        val fragment = MachineResultFragment().apply { arguments = intent.extras }
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }
}