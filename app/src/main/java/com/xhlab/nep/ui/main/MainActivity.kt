package com.xhlab.nep.ui.main

import android.os.Bundle
import com.xhlab.nep.R
import com.xhlab.nep.ui.ViewInit
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity(), ViewInit {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        setContentView(R.layout.activity_main)
        initView()
    }

    override fun initViewModel() {
    }

    override fun initView() {
    }
}
