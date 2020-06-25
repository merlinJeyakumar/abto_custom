package com.support.utills

import android.content.Context
import android.util.Log
import com.support.BuildConfig
import io.reactivex.Single
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

object Log {
    @JvmStatic
    fun i(TAG: String?, message: String?) {
        Log.d(TAG, message)
    }

    @JvmStatic
    fun e(TAG: String?, message: String?) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, message)
        }
    }

    @JvmStatic
    fun v(TAG: String?, message: String?) {
        Log.v(TAG, message)
    }

    @JvmStatic
    fun d(TAG: String?, message: String?) {
        Log.i(TAG, message)
    }

    fun w(TAG: String?, message: String?) {
        Log.i(TAG, message)
    }

    fun Context.getLogFile(): Single<File> {
        return Single.create {
            val logFile = File("${this.cacheDir}${File.separator}logs${File.separator}")
            logFile.mkdirs()

            val file = File(
                "${logFile.absolutePath}${File.separator}",
                "${System.currentTimeMillis()}_logDump.txt"
            )
            file.createNewFile()

            val process = Runtime.getRuntime().exec("logcat -d")
            val bufferedReader = BufferedReader(
                InputStreamReader(process.inputStream)
            )
            val log = StringBuilder()
            var line: String? = ""
            while (bufferedReader.readLine().also { line = it } != null) {
                log.append("\n$line")
            }
            file.writeText(log.toString())
            return@create it.onSuccess(file)
        }
    }

    fun Context.shareLogDirFile(): File {
        return File("${this.filesDir.absolutePath}${File.separator}share_logs${File.separator}").apply {
            this.mkdirs()
        }
    }
}