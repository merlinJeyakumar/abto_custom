package com.support.supportBaseClass

import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.snackbar.Snackbar
import com.support.R
import com.support.bcRecievers.ConnectivityReceiver
import io.reactivex.disposables.CompositeDisposable

abstract class BaseActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener,
    BaseView<BasePresenter> {
    private var connectivityChangeReceiver: ConnectivityReceiver? = null
    private var snackbar: Snackbar? = null
    private var intentFilter: IntentFilter? = null
    var compositeDisposable: CompositeDisposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        compositeDisposable = CompositeDisposable()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityChangeReceiver = ConnectivityReceiver()
            intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
            LocalBroadcastManager.getInstance(this).registerReceiver(connectivityChangeReceiver!!, intentFilter!!)
        }


    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {

        onNetworkStatusChanged(isConnected)
        if (!isConnected) {
            snackbar = Snackbar.make(
                findViewById<View>(android.R.id.content),
                "No Internet Connection",
                Snackbar.LENGTH_INDEFINITE
            )
            snackbar!!.show()
        } else {
            snackbar =
                Snackbar.make(findViewById<View>(android.R.id.content), "Internet Connected", Snackbar.LENGTH_SHORT)
            snackbar!!.setText("Internet Connected")
            snackbar!!.show()
        }
    }

    override fun onResume() {
        super.onResume()
        BaseApplication.instance?.setConnectivityListener(this)
        if (connectivityChangeReceiver != null) {
            this.registerReceiver(connectivityChangeReceiver, intentFilter)
        }
    }

    override fun onPause() {
        super.onPause()
        BaseApplication.instance?.setConnectivityListener(null)
        if (connectivityChangeReceiver != null) {
            this.unregisterReceiver(connectivityChangeReceiver)
        }
    }

    override fun onDestroy() {
        compositeDisposable?.dispose()
        super.onDestroy()
    }

    override fun setPresenter(presenter: BasePresenter) {

    }

    override fun showProcessing() {
        snackbar = Snackbar.make(findViewById<View>(android.R.id.content), R.string.loading, Snackbar.LENGTH_INDEFINITE)
        snackbar!!.show()
    }

    override fun hideProcessing() {
        snackbar =
            Snackbar.make(findViewById<View>(android.R.id.content), R.string.loading_success, Snackbar.LENGTH_SHORT)
        snackbar!!.show()
    }

    override fun showMessage(message: String) {
        snackbar = Snackbar.make(findViewById<View>(android.R.id.content), message, Snackbar.LENGTH_SHORT)
        snackbar!!.show()
    }

    override fun showMessage(message: Int) {
        snackbar = Snackbar.make(findViewById<View>(android.R.id.content), message, Snackbar.LENGTH_SHORT)
        snackbar!!.show()
    }

    override fun showErrorMessage(message: String) {
        snackbar = Snackbar.make(findViewById<View>(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE)
        snackbar!!.show()
    }

    override fun showErrorMessage(message: Int) {
        snackbar = Snackbar.make(findViewById<View>(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE)
        snackbar!!.show()
    }


    abstract fun onNetworkStatusChanged(isConnected: Boolean)

}
