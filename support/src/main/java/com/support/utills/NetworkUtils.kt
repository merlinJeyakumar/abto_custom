package com.support.utills

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.net.NetworkInfo


object NetworkUtils {

    /**
     * CHECK INTERNET CONNECTIVITY.
     *
     * @param context
     * @return
     */

    fun isInternetConnected(context: Context): Boolean {
        val connectivity = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivity != null) {
            val nInfo = connectivity.activeNetworkInfo
            if (nInfo != null) {
                //do your thing
                if (nInfo.type == ConnectivityManager.TYPE_WIFI) {
                    // connected to wifi
                    return true
                } else if (nInfo.type == ConnectivityManager.TYPE_MOBILE) {
                    // connected to the mobile provider's data plan
                    return true
                }
            }
            return false
        }
        return false
    }


    fun Context.isInternetAvailable(): Boolean {
        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var netInfo: NetworkInfo? = null
        netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnected && netInfo.isAvailable
    }

    /**
     * Check is wi-fi data on
     *
     * @param mContext
     * @return
     */
    fun isWifiNetwork(mContext: Context): Boolean {
        val connectivity = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nInfo = connectivity.activeNetworkInfo
        return nInfo != null && nInfo.type == ConnectivityManager.TYPE_WIFI
    }

    fun Context.isConnectedToWifi(): Boolean {
        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return capabilities.hasTransport(TRANSPORT_WIFI)
        } else {
            val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            return networkInfo.type == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected
        }
    }

    fun Context.isConnectedToMobileData(): Boolean {
        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return capabilities.hasTransport(TRANSPORT_CELLULAR)
        } else {
            val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            return networkInfo.type == ConnectivityManager.TYPE_MOBILE && networkInfo.isConnected
        }
    }
}