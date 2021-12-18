package com.xhlab.nep.shared.util

import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipInputStream

class JavaZipArchiver constructor(
    private val getInputStream: () -> InputStream
) : ZipArchiver {

    override suspend fun enumerate(each: (ZipArchiver.ZipEntry) -> Unit) {
        ZipInputStream(getInputStream()).use { zis ->
            var entry = zis.nextEntry
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            while (entry != null) {
                if (entry.isDirectory) {
                    continue
                }

                ByteArrayOutputStream().use { bos ->
                    var count = zis.read(buffer)
                    while (count != -1) {
                        bos.write(buffer, 0, count)
                        count = zis.read(buffer)
                    }
                    val entry = JavaZipEntry(
                        entry.name,
                        entry.isDirectory,
                        bos.toByteArray()
                    )
                    each(entry)
                }

                entry = zis.nextEntry
            }
        }
    }

    override suspend fun measureTotalEntryCount(): Int {
        var totalEntrySize = 0
        ZipInputStream(getInputStream()).use { zis ->
            while (zis.nextEntry != null) {
                totalEntrySize += 1
            }
        }
        return totalEntrySize
    }

    class JavaZipEntry(
        override val name: String,
        override val isDirectory: Boolean,
        val data: ByteArray
    ) : ZipArchiver.ZipEntry {

        override fun extract(to: File) {
            FileOutputStream(to).use { fos ->
                fos.write(data)
            }
        }
    }
}
