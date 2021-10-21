package com.xhlab.nep

import co.touchlab.kermit.Logger
import com.xhlab.nep.di.DaggerAppComponent
import com.xhlab.nep.logging.CrashlyticsTree
import com.xhlab.nep.shared.util.TimberLogger
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import timber.log.Timber

@Suppress("unused")
class NEPApp : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder()
            .application(this)
            .build().apply { inject(this@NEPApp) }
    }

    override fun onCreate() {
        super.onCreate()
        Logger.setLogWriters(TimberLogger())
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashlyticsTree())
        }
    }
}
