package com.xhlab.nep.shared.domain

import com.xhlab.multiplatform.domain.MediatorUseCase

abstract class BaseMediatorUseCase<in Params, Result> : MediatorUseCase<Params, Result>() {

    override fun onException(exception: Throwable) {
        // TODO: place logger here
    }
}
