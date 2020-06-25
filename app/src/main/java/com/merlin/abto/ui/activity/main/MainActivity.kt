package com.merlin.abto.ui.activity.main

import android.app.Notification.DEFAULT_SOUND
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import com.merlin.abto.R
import com.merlin.abto.abto.rxjava.AbtoRxEvents
import com.merlin.abto.databinding.LayoutMainBinding
import com.merlin.abto.extension.obtainViewModel
import com.merlin.abto.ui.activity.call.CallActivity
import com.support.baseApp.mvvm.MActionBarActivity
import com.support.rxJava.RxBus
import com.support.rxJava.Scheduler.ui
import kotlinx.android.synthetic.main.layout_main.*
import kotlin.random.Random

class MainActivity : MActionBarActivity<LayoutMainBinding, MainViewModel>() {
    private var MESSAGE_NOTIFICATION_CHANNEL = "Incoming message"

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
        viewModel.connectCall.observe(this@MainActivity, Observer {
            CallActivity.startActivity(this@MainActivity, it.first, it.second)
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
                .setContentText(messageContentText)
        val name: CharSequence =
            context.getString(R.string.app_name) // The user-visible name of the channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                MESSAGE_NOTIFICATION_CHANNEL,
                name,
                NotificationManager.IMPORTANCE_HIGH
            )
            mChannel.description = "Description"
            mChannel.setShowBadge(true)
            manager.createNotificationChannel(mChannel)
        }

        manager.notify(Math.abs(Random(10).nextInt()), builder.build())
    }
}
