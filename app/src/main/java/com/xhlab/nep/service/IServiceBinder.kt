package com.xhlab.nep.service

import android.app.Service

interface IServiceBinder<T : Service> {
    fun getService(): T
}