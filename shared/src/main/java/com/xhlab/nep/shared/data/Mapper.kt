package com.xhlab.nep.shared.data

interface Mapper<I, O> {
    fun map(element: I): O
}
