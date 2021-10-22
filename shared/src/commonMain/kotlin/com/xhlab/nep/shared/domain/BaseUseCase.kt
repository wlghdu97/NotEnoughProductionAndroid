package com.xhlab.nep.shared.domain

import co.touchlab.kermit.Logger
import com.xhlab.multiplatform.domain.UseCase

abstract class BaseUseCase<in Params, Result> : UseCase<Params, Result>() {

    override fun onException(exception: Throwable) {
        Logger.e("UseCase crashed.", exception)
    }
}
