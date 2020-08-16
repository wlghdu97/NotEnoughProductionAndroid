package com.xhlab.nep.ui.recipe

import android.os.Bundle
import com.xhlab.nep.R
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_machine_recipe_list.*

class MachineRecipeListActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_machine_recipe_list)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.title_recipe_list)
        val fragment = MachineRecipeListFragment().apply { arguments = intent.extras }
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }
}