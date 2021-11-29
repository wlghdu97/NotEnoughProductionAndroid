package com.xhlab.nep.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.xhlab.multiplatform.util.Resource
import com.xhlab.nep.R
import com.xhlab.nep.shared.domain.icon.IconUnzipUseCase
import com.xhlab.nep.shared.util.JavaZipArchiver
import com.xhlab.nep.ui.main.MainActivity
import dagger.android.AndroidInjection
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class IconUnzipService : Service() {

    private val binder = LocalBinder()

    @Inject
    lateinit var iconUnzipUseCase: IconUnzipUseCase

    private var iconUnzipJob: Job? = null
    private var isTaskDone = false

    val unzipLog by lazy { iconUnzipUseCase.observe() }

    private val pendingIntent by lazy {
        PendingIntent.getActivity(
            applicationContext,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun onCreate() {
        super.onCreate()
        AndroidInjection.inject(this)

        if (Build.VERSION.SDK_INT >= 26) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, "channel", importance)
            channel.setSound(null, null)
            channel.importance = NotificationManager.IMPORTANCE_LOW
            NotificationManagerCompat.from(this).createNotificationChannel(channel)
        }
        val builder = getNotificationBuilder()
        startForeground(NOTIFICATION_ID, builder.build())
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isTaskDone) {
            iconUnzipJob?.cancel()
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val fileUri = intent.getParcelableExtra<Uri>(ZIP_URI)
        if (fileUri != null) {
            CoroutineScope(SupervisorJob()).launch {
                val archiver = JavaZipArchiver {
                    contentResolver.openInputStream(fileUri)?.buffered()
                        ?: throw RuntimeException("Could not open file uri input stream.")
                }
                val job = iconUnzipUseCase.execute(Dispatchers.IO, archiver).apply {
                    iconUnzipJob = this
                }
                withContext(job) {
                    unzipLog.collectLatest {
                        updateProgress(it)
                    }
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    @Suppress("missingSuperCall")
    override fun onBind(intent: Intent) = binder

    override fun onTaskRemoved(rootIntent: Intent?) {
        NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID)
        stopSelf()
    }

    private suspend fun updateProgress(
        progress: Resource<IconUnzipUseCase.Progress>
    ) = withContext(Dispatchers.Main) {
        isTaskDone = progress.status != Resource.Status.LOADING

        if (isTaskDone) {
            stopSelf()
        }

        NotificationManagerCompat.from(this@IconUnzipService).apply {
            val builder = getNotificationBuilder().apply {
                setProgress(100, progress.data?.progress ?: 0, false)
                setContentTitle(
                    getString(
                        when (isTaskDone) {
                            true -> R.string.title_unzip_result
                            false -> R.string.title_unzip_notification
                        }
                    )
                )
                setContentText(progress.data?.message)
                setContentIntent(pendingIntent)
                setOngoing(!isTaskDone)
            }
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    private fun getNotificationBuilder(): NotificationCompat.Builder {
        return NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ic_nep_notification)
            setAutoCancel(true)
            priority = NotificationCompat.PRIORITY_LOW
        }
    }

    inner class LocalBinder : Binder(), IServiceBinder<IconUnzipService> {
        override fun getService() = this@IconUnzipService
    }

    companion object {
        const val ZIP_URI = "zip_uri"
    }
}
