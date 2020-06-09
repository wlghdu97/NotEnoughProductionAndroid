package com.xhlab.nep.tests.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

object LiveDataTestUtil {

    @Throws(InterruptedException::class)
    fun <T> getValue(liveData: LiveData<T>, countDown: Int = 1): T? {
        var data: T? = null
        val latch = CountDownLatch(countDown)
        val observer = object : Observer<T> {
            override fun onChanged(t: T) {
                data = t
                latch.countDown()
                if (latch.count == 0L) {
                    liveData.removeObserver(this)
                }
            }
        }
        liveData.observeForever(observer)
        latch.await(5, TimeUnit.SECONDS)

        return data
    }
}