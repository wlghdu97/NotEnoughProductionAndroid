package com.xhlab.nep

import com.crashlytics.android.Crashlytics
import com.xhlab.nep.di.DaggerAppComponent
import com.xhlab.nep.logging.CrashlyticsTree
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import io.fabric.sdk.android.Fabric
import timber.log.Timber

@Suppress("unused")
class NEPApp : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder()
            .application(this)
            .build().apply { inject(this@NEPApp) }
    }

    override fun onCreate() {
        initFabric()
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashlyticsTree())
        }
    }

    private fun initFabric() {
        Fabric.with(this, Crashlytics())
    }
}