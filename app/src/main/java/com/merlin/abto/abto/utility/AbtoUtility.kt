package com.merlin.abto.abto.utility

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Environment
import androidx.core.app.NotificationCompat
import com.merlin.abto.R
import com.merlin.abto.core.AppController
import com.support.dateClass.DateUtils
import com.support.utills.Log.shareLogDirFile
import com.support.utills.ZipManager
import io.reactivex.Single
import java.io.File

var FOREGROUND_NOTIFICATION_CHANNEL_ID = "Call Service"

fun getSipProps(text: String): MutableList<String> {
    val sipIdDomain = text.split(";")[0]
    val sipPassword = text.split(";")[1]

    val sipId = sipIdDomain.split("@")[0]
    val sipDomain = sipIdDomain.split("@")[1]

    return mutableListOf<String>().apply {
        this.add(sipId)
        this.add(sipDomain)
        this.add(sipPassword)
    }
}

fun getSipRemoteAddress(remoteAddress:String): String {
    return remoteAddress.replace("sip:","").replace("<","").replace(">","")
}
fun Context.getForegroundNotification(): Notification? {
    val channelId = FOREGROUND_NOTIFICATION_CHANNEL_ID
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            channelId,
            FOREGROUND_NOTIFICATION_CHANNEL_ID,
            NotificationManager.IMPORTANCE_NONE
        )
        notificationChannel.lightColor = Color.BLUE
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(notificationChannel)
    }

    return NotificationCompat.Builder(this, channelId).setOngoing(true)
        .setSmallIcon(R.drawable.ic_launcher_background)
        .setPriority(NotificationCompat.PRIORITY_MIN)
        .setCategory(Notification.CATEGORY_CALL)
        .build()
}

fun getAbtoLogs(): Single<File> {
    return Single.create<File> {
        val zipManager = ZipManager()
        zipManager.dirChecker(getAbtoLogsPath().absolutePath)
        val fileList =
            mutableListOf<File>().apply { addAll(getAbtoLogsPath().listFiles()!!.toList()) }
        val zipFile = File(
            AppController.instance.shareLogDirFile(),
            "abtoLogs_${DateUtils.getTodayDateTime()}.zip"
        )
        zipManager.zip(fileList, zipFile)
        it.onSuccess(zipFile)
    }
}

fun getAbtoLogsPath(): File {
    return File("${Environment.getExternalStorageDirectory()?.absolutePath}${File.separator}${AppController.instance.packageName}/logs").apply {
        this.mkdirs()
    }
}