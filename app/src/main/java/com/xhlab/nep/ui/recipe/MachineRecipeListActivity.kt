package com.xhlab.nep.ui.recipe

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.xhlab.nep.R
import com.xhlab.nep.databinding.ActivityMachineRecipeListBinding
import com.xhlab.nep.ui.recipe.MachineRecipeListFragment.Companion.ELEMENT_ID
import com.xhlab.nep.ui.recipe.MachineRecipeListFragment.Companion.MACHINE_ID
import dagger.android.support.DaggerAppCompatActivity

class MachineRecipeListActivity : DaggerAppCompatActivity() {

    private lateinit var binding: ActivityMachineRecipeListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMachineRecipeListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setTitle(R.string.title_recipe_list)
        val fragment = MachineRecipeListFragment().apply { arguments = intent.extras }
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    companion object {
        fun Context.navigateToMachineRecipeListActivity(elementId: Long, machineId: Int?) {
            startActivity(Intent(this, MachineRecipeListActivity::class.java).apply {
                putExtra(ELEMENT_ID, elementId)
                putExtra(MACHINE_ID, machineId ?: -1)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }
}
