package com.xhlab.nep.ui.main.machines.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.xhlab.nep.R
import com.xhlab.nep.ui.main.machines.details.MachineResultFragment.Companion.MACHINE_ID
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

    companion object {
        fun Context.navigateToMachineResultActivity(machineId: Int) {
            startActivity(Intent(this, MachineResultActivity::class.java).apply {
                putExtra(MACHINE_ID, machineId)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }
}
