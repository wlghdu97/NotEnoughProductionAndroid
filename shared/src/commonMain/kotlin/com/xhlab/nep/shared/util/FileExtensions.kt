package com.xhlab.nep.shared.util

// these are here to avoid "duplicate JVM class name"

val File.isDirectory: Boolean
    get() = isDirectory()

val File.name: String
    get() = getName()
