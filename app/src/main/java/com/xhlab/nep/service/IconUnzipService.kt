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
import androidx.lifecycle.Observer
import com.xhlab.nep.R
import com.xhlab.nep.shared.domain.icon.IconUnzipUseCase
import com.xhlab.nep.shared.util.Resource
import com.xhlab.nep.ui.main.MainActivity
import dagger.android.AndroidInjection
import javax.inject.Inject

class IconUnzipService : Service() {

    private val binder = LocalBinder()

    @Inject
    lateinit var iconUnzipUseCase: IconUnzipUseCase

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

    private val logObserver = Observer<Resource<IconUnzipUseCase.Progress>> {
        isTaskDone = it.status != Resource.Status.LOADING

        if (isTaskDone) {
            stopSelf()
        }

        NotificationManagerCompat.from(this).apply {
            val builder = getNotificationBuilder().apply {
                setProgress(100, it.data?.progress ?: 0, false)
                setContentTitle(getString(when (isTaskDone) {
                    true -> R.string.title_unzip_result
                    false -> R.string.title_unzip_notification
                }))
                setContentText(it.data?.message)
                setContentIntent(pendingIntent)
                setOngoing(!isTaskDone)
            }
            notify(NOTIFICATION_ID, builder.build())
        }
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
            iconUnzipUseCase.cancel()
        }
        unzipLog.removeObserver(logObserver)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        unzipLog.observeForever(logObserver)
        val fileUri = intent.getParcelableExtra<Uri>(ZIP_URI)
        if (fileUri != null) {
            iconUnzipUseCase.execute(fileUri)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    @Suppress("missingSuperCall")
    override fun onBind(intent: Intent) = binder

    override fun onTaskRemoved(rootIntent: Intent?) {
        NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID)
        stopSelf()
    }

    private fun getNotificationBuilder(): NotificationCompat.Builder {
        return NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setSmallIcon(R.mipmap.ic_launcher)
            setAutoCancel(true)
            priority = NotificationCompat.PRIORITY_LOW
        }
    }

    inner class LocalBinder : Binder(), IServiceBinder<IconUnzipService> {
        override fun getService() = this@IconUnzipService
    }

    companion object {
        private const val CHANNEL_ID = "icon_unzip_channel_id"
        private const val NOTIFICATION_ID = 102
        const val ZIP_URI = "zip_uri"
    }
}