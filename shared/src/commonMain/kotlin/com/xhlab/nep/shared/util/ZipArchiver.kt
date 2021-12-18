package com.xhlab.nep.shared.util

interface ZipArchiver {
    suspend fun enumerate(each: (ZipEntry) -> Unit)
    suspend fun measureTotalEntryCount(): Int

    interface ZipEntry {
        val name: String
        val isDirectory: Boolean
        fun extract(to: File)
    }
}
