package com.xhlab.nep

import com.xhlab.nep.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

@Suppress("unused")
class NEPApp : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder()
            .application(this)
            .build().apply { inject(this@NEPApp) }
    }
}