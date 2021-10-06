package com.xhlab.nep.shared.domain.icon

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveDataScope
import androidx.lifecycle.liveData
import com.xhlab.nep.shared.R
import com.xhlab.nep.shared.domain.Cancelable
import com.xhlab.nep.shared.domain.MediatorUseCase
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import timber.log.Timber
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class IconUnzipUseCase @Inject constructor(
    private val context: Context,
    private val generalPreference: GeneralPreference
) : MediatorUseCase<Uri, IconUnzipUseCase.Progress>(), Cancelable {

    private var job: Job? = null
    private val outputDir = context.getExternalFilesDir("icons")

    override fun executeInternal(params: Uri) = liveData<Resource<Progress>>(Dispatchers.IO) {
        // save job to support cancellation
        job = coroutineContext[Job]

        // mark icon status is dirty
        generalPreference.setIconLoaded(false)

        // delete all previous elements
        emitProgress(context.getString(R.string.txt_delete_previous_files))
        deleteFiles(outputDir)

        // create parent dir
        if (outputDir?.exists() == false) {
            outputDir.mkdir()
        }

        fun getBufferedStream(): BufferedInputStream? {
            return context.contentResolver.openInputStream(params)?.buffered()
        }

        // unzip icon files
        val startTime = System.currentTimeMillis()
        var totalEntrySize = 0
        emitProgress(context.getString(R.string.txt_calculating_zip_entries))
        ZipInputStream(getBufferedStream()).use { zis ->
            while (zis.nextEntry != null) {
                totalEntrySize += 1
            }
        }
        ZipInputStream(getBufferedStream()).use { zis ->
            var entry = zis.nextEntry
            var entryCount = 0
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            while (entry != null) {
                if (entry.isDirectory) {
                    continue
                }
                val file = File(outputDir, entry.name)
                FileOutputStream(file).use { fos ->
                    var count = zis.read(buffer)
                    while (count != -1) {
                        fos.write(buffer, 0, count)
                        count = zis.read(buffer)
                    }
                }

                entryCount += 1
                val progress = ((entryCount.toFloat() / totalEntrySize.toFloat()) * 100).toInt()
                emitProgress(String.format(context.getString(R.string.form_unzip_file), file.name), progress)
                entry = zis.nextEntry
            }
        }

        // mark icons are successfully loaded
        generalPreference.setIconLoaded(true)

        val elapsedTime = System.currentTimeMillis() - startTime
        emitProgress(
            log = String.format(context.getString(R.string.form_work_done), elapsedTime / 1000),
            progress = 100,
            status = Resource.Status.SUCCESS
        )
    }

    override fun cancel() {
        job?.cancel()

        val message = context.getString(R.string.txt_job_canceled)
        Timber.i(message)
        result.postValue(Resource.success(Progress(0, message)))
    }

    private fun deleteFiles(dir: File?) {
        if (dir != null && dir.exists()) {
            if (dir.isDirectory) {
                for (child in dir.list() ?: emptyArray()) {
                    deleteFiles(File(dir, child))
                }
            }
            dir.delete()
        }
    }

    private suspend fun LiveDataScope<Resource<Progress>>.emitProgress(
        log: String,
        progress: Int = 0,
        status: Resource.Status = Resource.Status.LOADING
    ) {
        Timber.i(log)
        emit(Resource(status, Progress(progress, log), null))
    }

    data class Progress(val progress: Int, val message: String)
}
