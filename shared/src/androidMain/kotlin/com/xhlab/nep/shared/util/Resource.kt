package com.xhlab.nep.shared.util

fun <T> Resource<T>.isSuccessful(): Boolean =
    (data != null || data is Unit) && (status == Resource.Status.SUCCESS)

data class Resource<out T>(val status: Status, val data: T?, val throwable: Throwable?) {

    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(t: Throwable?): Resource<T> {
            return Resource(Status.ERROR, null, t)
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }
    }

    enum class Status {
        ERROR, LOADING, SUCCESS;
    }
}
