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
import com.xhlab.nep.shared.domain.parser.ParseRecipeUseCase
import com.xhlab.nep.shared.util.Resource
import dagger.android.AndroidInjection
import javax.inject.Inject

class ParseRecipeService @Inject constructor() : Service() {

    private val binder = LocalBinder()

    @Inject
    lateinit var parseRecipeUseCase: ParseRecipeUseCase

    private var isTaskDone = false

    val parseLog by lazy { parseRecipeUseCase.observe() }

    private val pendingIntent by lazy {
        PendingIntent.getActivity(applicationContext, 0, Intent(), PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private val logObserver = Observer<Resource<String>> {
        isTaskDone = it.status != Resource.Status.LOADING

        if (isTaskDone) {
            stopSelf()
        }

        NotificationManagerCompat.from(this).apply {
            val builder = getNotificationBuilder().apply {
                setProgress(100, 100, !isTaskDone)
                setContentTitle(getString(when (isTaskDone) {
                    true -> R.string.title_parsing_result
                    false -> R.string.title_parsing_notification
                }))
                setContentText(it.data)
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
            parseRecipeUseCase.cancel()
        }
        parseLog.removeObserver(logObserver)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        parseLog.observeForever(logObserver)
        val fileUri = intent.getParcelableExtra<Uri>(JSON_URI)
        if (fileUri != null) {
            val inputStream = contentResolver.openInputStream(fileUri)
            if (inputStream != null) {
                parseRecipeUseCase.execute(inputStream)
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

    private fun getNotificationBuilder(): NotificationCompat.Builder {
        return NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setSmallIcon(R.mipmap.ic_launcher)
            setAutoCancel(true)
            priority = NotificationCompat.PRIORITY_DEFAULT
        }
    }

    inner class LocalBinder : Binder() {
        fun getService() = this@ParseRecipeService
    }

    companion object {
        private const val CHANNEL_ID = "parse_recipe_channel_id"
        private const val NOTIFICATION_ID = 101
        const val JSON_URI = "json_uri"
    }
}