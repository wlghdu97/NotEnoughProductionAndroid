package com.xhlab.nep.ui.recipe

import android.os.Bundle
import com.xhlab.nep.R
import com.xhlab.nep.databinding.ActivityMachineRecipeListBinding
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
}
