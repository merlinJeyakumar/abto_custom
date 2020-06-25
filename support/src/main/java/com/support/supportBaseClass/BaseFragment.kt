package com.support.supportBaseClass

import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.snackbar.Snackbar
import com.support.bcRecievers.ConnectivityReceiver

import java.util.Objects

abstract class BaseFragment : Fragment(), ConnectivityReceiver.ConnectivityReceiverListener {
    private var connectivityChangeReceiver: ConnectivityReceiver? = null
    private var snackbar: Snackbar? = null
    private var intentFilter: IntentFilter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityChangeReceiver = ConnectivityReceiver()
            intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
            LocalBroadcastManager.getInstance(Objects.requireNonNull<FragmentActivity>(activity))
                .registerReceiver(connectivityChangeReceiver!!, intentFilter!!)
        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {

        onNetworkStatusChanged(isConnected)


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        (BaseApplication.instance)?.setConnectivityListener(this)
        if (connectivityChangeReceiver != null) {
            LocalBroadcastManager.getInstance(Objects.requireNonNull<FragmentActivity>(activity))
                .registerReceiver(connectivityChangeReceiver!!, intentFilter!!)
        }
    }

    override fun onPause() {
        super.onPause()
        (BaseApplication.instance)?.clearInstance()
        if (connectivityChangeReceiver != null) {
            LocalBroadcastManager.getInstance(Objects.requireNonNull<FragmentActivity>(activity))
                .unregisterReceiver(connectivityChangeReceiver!!)
        }
    }


    abstract fun onNetworkStatusChanged(isConnected: Boolean)


}
