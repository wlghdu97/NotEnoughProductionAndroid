package com.xhlab.nep.ui.main.process

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.xhlab.nep.R
import com.xhlab.nep.di.ViewModelFactory
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class ProcessListFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_process_list, container, false)
    }
}