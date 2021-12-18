package com.xhlab.nep.shared.util

expect class File constructor(parent: File?, child: String) {
    fun getName(): String
    fun isDirectory(): Boolean
    fun exists(): Boolean
    fun mkdir(): Boolean
    fun list(): Array<String>?
    fun delete(): Boolean
}
