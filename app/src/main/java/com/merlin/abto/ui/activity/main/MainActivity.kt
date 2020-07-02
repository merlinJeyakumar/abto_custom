package com.merlin.abto.ui.activity.main

import android.app.Notification.DEFAULT_SOUND
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import com.merlin.abto.R
import com.merlin.abto.abto.rxjava.AbtoRxEvents
import com.merlin.abto.databinding.LayoutMainBinding
import com.merlin.abto.extension.obtainViewModel
import com.merlin.abto.ui.activity.call.CallActivity
import com.merlin.abto.ui.activity.configuration.ConfigurationActivity
import com.merlin.abto.ui.activity.register.RegisterActivity
import com.support.baseApp.mvvm.MActionBarActivity
import com.support.dialog.getConfirmationDialog
import com.support.rxJava.RxBus
import com.support.rxJava.Scheduler.ui
import kotlinx.android.synthetic.main.layout_main.*
import org.jetbrains.anko.toast
import kotlin.math.abs
import kotlin.random.Random

class MainActivity : MActionBarActivity<LayoutMainBinding, MainViewModel>() {
    private var menu_refresh: MenuItem? = null
    private var MESSAGE_NOTIFICATION_CHANNEL = "Incoming message"
    private var INCOMING_SIP_IDENTITY = "INCOMING_SIP_IDENTITY"

    override fun getLayoutId(): Int {
        return R.layout.layout_main
    }

    override fun setUpUI(savedInstanceState: Bundle?) {
        viewModel.subscribe()

        initView()
        initObserver()
    }

    private fun initView() {
        edSipId.setAdapter(viewModel.initAdapterAutoComplete())
    }

    private fun initObserver() {
        viewModel.connectCall.observe(this, Observer {
            CallActivity.startActivity(this, it.first, it.second)
        })
        viewModel.unregisterLiveData.observe(this, Observer {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        })
        viewModel.connectionStatus.observe(this, Observer {
            menu_refresh?.isVisible = !it
        })
        addRxCall(RxBus.listen(AbtoRxEvents.MessageReceived::class.java)
            .observeOn(ui())
            .subscribeOn(ui())
            .subscribe {
                buildMessageNotification(it.senderSipId, it.message)
            })
    }

    override fun getHeaderTitle(): String {
        return resources.getString(R.string.app_name)
    }

    override fun isSupportBackOption(): Boolean {
        return false
    }

    override fun initializeViewModel(): MainViewModel {
        return obtainViewModel(MainViewModel::class.java).apply {
            binding.viewmodel = this
        }
    }

    override fun onNetworkStatusChanged(isConnected: Boolean) {
    }

    private fun buildMessageNotification(messageSender: String, messageContentText: String) {
        val manager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, MESSAGE_NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.ic_twotone_message_24)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSubText("Message received")
                .setShowWhen(true)
                .setStyle(NotificationCompat.BigTextStyle().setBigContentTitle(messageSender))
                .setDefaults(DEFAULT_SOUND)
                .setVibrate(longArrayOf(100, 100))
                .setContentIntent(
                    PendingIntent.getActivity(
                        this,
                        0,
                        Intent(this, MainActivity::class.java).putExtra(
                            INCOMING_SIP_IDENTITY,
                            messageSender
                        ),
                        0
                    )
                )
                .setContentText(messageContentText)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                MESSAGE_NOTIFICATION_CHANNEL,
                context.getString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            mChannel.description = "Description"
            mChannel.setShowBadge(true)
            manager.createNotificationChannel(mChannel)
        }

        manager.notify(abs(Random(10).nextInt()), builder.build())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        menu_refresh = menu.findItem(R.id.menu_refresh)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_unregister -> viewModel.unregister()
            R.id.menu_configuration -> startActivity(
                Intent(
                    this,
                    ConfigurationActivity::class.java
                )
            )
            R.id.menu_refresh -> viewModel.initPermission()
        }
        return super.onOptionsItemSelected(item)
    }

    fun clearLog(view: View? = null) {
        addRxCall(getConfirmationDialog(message = "Sure to delete?").subscribe({
            if (it) {
                viewModel.clearLog()
            }
        }, {
            it.printStackTrace()
        }))
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        overridePendingTransition(0, 0)
        intent?.getStringExtra(INCOMING_SIP_IDENTITY)?.replace("<", "")?.replace(">", "")?.let {
            viewModel.setSipAddress(
                it
            )
        }
    }
}
