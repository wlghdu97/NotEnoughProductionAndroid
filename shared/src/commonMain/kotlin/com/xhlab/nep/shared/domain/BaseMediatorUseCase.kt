package com.xhlab.nep.shared.domain

import co.touchlab.kermit.Logger
import com.xhlab.multiplatform.domain.MediatorUseCase

abstract class BaseMediatorUseCase<in Params, Result> : MediatorUseCase<Params, Result>() {

    override fun onException(exception: Throwable) {
        Logger.e("MediatorUseCase crashed.", exception)
    }
}
