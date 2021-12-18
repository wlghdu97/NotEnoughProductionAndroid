package com.xhlab.nep.shared.domain.icon

import co.touchlab.kermit.Logger
import com.xhlab.nep.MR
import com.xhlab.nep.shared.domain.BaseMediatorUseCase
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.util.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kr.sparkweb.multiplatform.domain.Cancellable
import kr.sparkweb.multiplatform.util.Resource

class IconUnzipUseCase constructor(
    private val generalPreference: GeneralPreference,
    private val stringResolver: StringResolver,
    private val outputDir: File,
    private val io: CoroutineDispatcher
) : BaseMediatorUseCase<ZipArchiver, IconUnzipUseCase.Progress>(), Cancellable {

    override suspend fun executeInternal(params: ZipArchiver) = channelFlow {
        // mark icon status is dirty
        generalPreference.setIconLoaded(false)

        // delete all previous elements
        emitProgress(stringResolver.getString(MR.strings.txt_delete_previous_files))
        deleteFiles(outputDir)

        // create parent dir
        if (!outputDir.exists()) {
            outputDir.mkdir()
        }

        // unzip icon files
        val startTime = epochMillis
        emitProgress(stringResolver.getString(MR.strings.txt_calculating_zip_entries))
        val totalEntrySize = params.measureTotalEntryCount()

        Logger.i("total entry size : $totalEntrySize")

        var entryCount = 0
        params.enumerate { entry ->
            if (!entry.isDirectory) {
                val file = File(outputDir, entry.name)
                entry.extract(file)
                entryCount += 1
                val progress = ((entryCount.toFloat() / totalEntrySize.toFloat()) * 100).toInt()
                emitProgress(
                    stringResolver.formatString(MR.strings.form_unzip_file, file.name),
                    progress
                )
            }
        }

        // mark icons are successfully loaded
        generalPreference.setIconLoaded(true)

        val elapsedTime = epochMillis - startTime
        emitProgress(
            log = stringResolver.formatString(MR.strings.form_work_done, elapsedTime / 1000),
            progress = 100,
            status = Resource.Status.SUCCESS
        )
    }.flowOn(io)

    override fun onCancellation() {
        val message = stringResolver.getString(MR.strings.txt_job_canceled)
        Logger.i(message)
        result.value = Resource.success(Progress(0, message))
    }

    private fun deleteFiles(dir: File?) {
        if (dir != null && dir.exists()) {
            if (dir.isDirectory) {
                for (child in dir.list() ?: emptyArray()) {
                    Logger.i("deleting : ${dir.name} $child")
                    deleteFiles(File(dir, child))
                }
            }
            dir.delete()
        }
    }

    @ExperimentalCoroutinesApi
    private fun ProducerScope<Resource<Progress>>.emitProgress(
        log: String,
        progress: Int = 0,
        status: Resource.Status = Resource.Status.LOADING
    ) {
        Logger.i(log)
        trySend(Resource(status, Progress(progress, log), null))
    }

    data class Progress(val progress: Int, val message: String)
}
