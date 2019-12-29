package com.xhlab.nep.shared.preference

interface GeneralPreference {
    fun getDBLoaded(): Boolean
    fun setDBLoaded(value: Boolean)
}