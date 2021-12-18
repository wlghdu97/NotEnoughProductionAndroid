package com.xhlab.nep.shared.util

import platform.Foundation.*

actual class File {
    val url: NSURL

    constructor(fileURLWithPath: String) {
        this.url = NSURL(fileURLWithPath = fileURLWithPath)
    }

    actual constructor(parent: File?, child: String) {
        this.url = parent?.url?.URLByAppendingPathComponent(child)
            ?: throw NSURLCreationException()
    }

    actual fun getName(): String {
        return url.lastPathComponent!!
    }

    actual fun isDirectory(): Boolean {
        return (url.resourceValuesForKeys(
            keys = listOf(NSURLIsDirectoryKey),
            error = null
        )?.get(NSURLIsDirectoryKey) as? NSNumber)?.boolValue ?: false
    }

    actual fun exists(): Boolean {
        return url.path?.let {
            NSFileManager.defaultManager.fileExistsAtPath(it)
        } ?: false
    }

    actual fun mkdir(): Boolean {
        return NSFileManager.defaultManager.createDirectoryAtURL(url, false, null, null)
    }

    @Suppress("unchecked_cast")
    actual fun list(): Array<String>? {

        fun NSURL.getPathComponents(): List<String> {
            return (pathComponents as? List<String>) ?: emptyList()
        }

        val parentPathComponents = url.getPathComponents().toSet()
        val files = NSFileManager.defaultManager.contentsOfDirectoryAtURL(
            url = url,
            includingPropertiesForKeys = null,
            options = NSDirectoryEnumerationSkipsHiddenFiles,
            error = null
        )

        return files?.mapNotNull {
            (it as? NSURL)?.getPathComponents()
                ?.subtract(parentPathComponents)
                ?.joinToString("/")
        }?.toTypedArray()
    }

    actual fun delete(): Boolean {
        return NSFileManager.defaultManager.removeItemAtURL(url, null)
    }

    class NSURLCreationException : Exception()
}
