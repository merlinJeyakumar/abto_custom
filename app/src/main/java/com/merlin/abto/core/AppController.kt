package com.merlin.abto.core

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.merlin.abto.abto.receiver.CallEventsReceiver
import com.merlin.abto.ui.activity.CrashActivity
import org.abtollc.sdk.AbtoApplication
import org.abtollc.sdk.AbtoPhone
import org.abtollc.utils.AbtoAppInBackgroundHandler
import org.jetbrains.anko.runOnUiThread

class AppController : AbtoApplication(), LifecycleObserver {

    private val callEventsReceiver = CallEventsReceiver()
    private val TAG = javaClass.simpleName
    private var appInBackgroundHandler: AbtoAppInBackgroundHandler? = null

    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(context)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    override fun onCreate() {
        super.onCreate()
        initialize()
    }

    private fun handleUncaughtException(
        thread: Thread,
        e: Throwable
    ) {
        runOnUiThread {
            val intent =
                Intent() //this has to match your intent filter
            intent.setClass(applicationContext, CrashActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(CrashActivity.CRASH_ISSUE, e.message)
            startActivity(intent)
        }
    }

    private fun initialize() {
        instance = this
        /*Thread.setDefaultUncaughtExceptionHandler { thread: Thread, e: Throwable ->
            handleUncaughtException(
                thread,
                e
            )
        }*/
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        appInBackgroundHandler = AbtoAppInBackgroundHandler()
        registerReceiver(callEventsReceiver, IntentFilter(AbtoPhone.ACTION_ABTO_CALL_EVENT))
        registerActivityLifecycleCallbacks(appInBackgroundHandler)
    }

    val isAppInBackground: Boolean
        get() = appInBackgroundHandler?.isAppInBackground!!

    companion object {
        var isSipRegistered = false
        lateinit var instance: AppController
        var isCallsAccepted = false
    }

}