package com.support.bcRecievers

import android.annotation.SuppressLint
import android.net.Network
import android.net.ConnectivityManager
import android.os.PowerManager
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import io.reactivex.BackpressureStrategy
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject


class MarshmallowNetworkObservingStrategy : NetworkObservingStrategy {

    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private val connectivitySubject = PublishSubject.create<Connectivity>()
    private var idleReceiver: BroadcastReceiver? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun observeNetworkConnectivity(context: Context): Observable<Connectivity> {
        val service = Context.CONNECTIVITY_SERVICE
        val manager = context.getSystemService(service) as ConnectivityManager
        networkCallback = createNetworkCallback(context)

        registerIdleReceiver(context)

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_WIFI_P2P)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
            .build()

        manager.registerNetworkCallback(request, networkCallback)

        return connectivitySubject.toFlowable(BackpressureStrategy.LATEST).doOnCancel {
            tryToUnregisterCallback(manager)
            tryToUnregisterReceiver(context)
        }.startWith(Connectivity.create(context)).distinctUntilChanged().toObservable()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    protected fun registerIdleReceiver(context: Context) {
        val filter = IntentFilter(PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED)
        idleReceiver = createBroadcastReceiver()
        context.registerReceiver(idleReceiver, filter)
    }

    @NonNull
    protected fun createBroadcastReceiver(): BroadcastReceiver {
        return object : BroadcastReceiver() {
            @SuppressLint("NewApi")
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onReceive(context: Context, intent: Intent) {
                if (isIdleMode(context)) {
                    onNext(Connectivity.create())
                } else {
                    onNext(Connectivity.create(context))
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    protected fun isIdleMode(context: Context): Boolean {
        val packageName = context.packageName
        val manager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val isIgnoringOptimizations = manager.isIgnoringBatteryOptimizations(packageName)
        return manager.isDeviceIdleMode && !isIgnoringOptimizations
    }

    protected fun tryToUnregisterCallback(manager: ConnectivityManager) {
        try {
            manager.unregisterNetworkCallback(networkCallback)
        } catch (exception: Exception) {
            onError(ERROR_MSG_NETWORK_CALLBACK, exception)
        }

    }

    protected fun tryToUnregisterReceiver(context: Context) {
        try {
            context.unregisterReceiver(idleReceiver)
        } catch (exception: Exception) {
            onError(ERROR_MSG_RECEIVER, exception)
        }

    }

    override fun onError(message: String, exception: Exception) {
        ""
    }

    protected fun createNetworkCallback(context: Context): ConnectivityManager.NetworkCallback {
        return object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                onNext(Connectivity.create(context))
            }

            override fun onLost(network: Network) {
                onNext(Connectivity.create(context))
            }
        }
    }

    protected fun onNext(connectivity: Connectivity) {
        connectivitySubject.onNext(connectivity)
    }

    companion object {
        protected val ERROR_MSG_NETWORK_CALLBACK = "could not unregister network callback"
        protected val ERROR_MSG_RECEIVER = "could not unregister receiver"
    }
}