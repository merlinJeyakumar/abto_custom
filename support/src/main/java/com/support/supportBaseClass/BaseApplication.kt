package com.support.supportBaseClass


import androidx.multidex.MultiDexApplication

import com.support.bcRecievers.ConnectivityReceiver

open class BaseApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }


    fun setConnectivityListener(listener: ConnectivityReceiver.ConnectivityReceiverListener?) {
        ConnectivityReceiver.connectivityReceiverListener = listener
    }


    companion object {

        @get:Synchronized
        var instance: BaseApplication? = null
            private set


    }

    fun clearInstance(){
        instance = null
    }
}
