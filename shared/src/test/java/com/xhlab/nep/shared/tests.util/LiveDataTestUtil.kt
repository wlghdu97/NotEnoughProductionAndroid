package com.xhlab.nep.shared.tests.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

object LiveDataTestUtil {

    @Throws(InterruptedException::class)
    fun <T> getValue(liveData: LiveData<T>): T? {
        var data: T? = null
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(t: T) {
                data = t
                latch.countDown()
                liveData.removeObserver(this)
            }
        }
        liveData.observeForever(observer)
        latch.await(5, TimeUnit.SECONDS)

        return data
    }
}