package com.merlin.abto.abto.receiver

import android.app.Notification
import android.app.Notification.FLAG_NO_CLEAR
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.RemoteException
import android.util.Log
import androidx.core.app.NotificationCompat
import com.merlin.abto.core.AppController.Companion.instance
import com.merlin.abto.R
import com.merlin.abto.ui.activity.call.CallActivity
import com.merlin.abto.ui.activity.call.CallActivity.Companion.IS_ATTENDED_CALL
import com.merlin.abto.ui.activity.call.CallActivity.Companion.INCOMING_CALL_NOTIFICATION
import com.merlin.abto.ui.activity.call.CallActivity.Companion.IS_INCOMING_CALL
import com.merlin.abto.ui.activity.call.CallActivity.Companion.IS_VIDEO_CALL
import org.abtollc.sdk.AbtoPhone

class CallEventsReceiver : BroadcastReceiver() {
    private val TAG: String = this.javaClass.simpleName

    override fun onReceive(context: Context, intent: Intent) {
        Log.e(TAG, "CallEventReceiver: ${intent.extras?.getInt(AbtoPhone.CODE)}")

        val bundle = intent.extras ?: return
        if (bundle.getBoolean(AbtoPhone.IS_INCOMING, false)) {

            // Incoming call
            buildIncomingCallNotification(context, bundle)
        } else if (bundle.getBoolean(KEY_REJECT_CALL, false)) {

            // Reject call
            val callId = bundle.getInt(AbtoPhone.CALL_ID)
            cancelIncCallNotification(context, callId)
            try {
                instance.abtoPhone.rejectCall(callId)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        } else if (bundle.getInt(AbtoPhone.CODE) == -1) {

            // Cancel call
            val callId = bundle.getInt(AbtoPhone.CALL_ID)
            cancelIncCallNotification(context, callId)
        }
    }

    private fun buildIncomingCallNotification(context: Context, bundle: Bundle) {
        val intent = Intent(context, CallActivity::class.java)
        intent.putExtra(IS_INCOMING_CALL, true)
        intent.putExtra(IS_VIDEO_CALL, bundle.getBoolean(AbtoPhone.HAS_VIDEO, false))
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.putExtra(AbtoPhone.CALL_ID, bundle.getInt(AbtoPhone.CALL_ID))
        intent.putExtras(bundle)
        if (!instance.isAppInBackground) { //App is foreground - start activity
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            return
        }
        val isVideoCall = bundle.getBoolean(AbtoPhone.HAS_VIDEO, false)
        val title =
            if (isVideoCall) "Incoming call" else "Incoming call" //FIXME: DETERMINE_VIDEO_OR_VOICE_CALL
        val remoteContact = bundle.getString(AbtoPhone.REMOTE_CONTACT)
        val callId = bundle.getInt(AbtoPhone.CALL_ID)
        val notificationId = NOTIFICATION_INCOMING_CALL_ID + callId
        val dialerName = "$remoteContact"

        // Create channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && channelCall == null) {
            channelCall = NotificationChannel(
                CHANEL_CALL_ID,
                context.getString(R.string.app_name) + " Call",
                NotificationManager.IMPORTANCE_HIGH
            )
            channelCall?.description = title
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channelCall!!)
        }

        // Intent for launch CallActivity
        intent.putExtra(INCOMING_CALL_NOTIFICATION, notificationId)
        val pendingIntent =
            PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Intent for pickup audio call
        intent.putExtra(IS_ATTENDED_CALL, true)

        // Intent for reject call
        val rejectCallIntent = Intent()
        rejectCallIntent.setPackage(context.packageName)
        rejectCallIntent.action = AbtoPhone.ACTION_ABTO_CALL_EVENT
        rejectCallIntent.putExtra(AbtoPhone.CALL_ID, callId)
        rejectCallIntent.putExtra(KEY_REJECT_CALL, true)
        val pendingRejectCall = PendingIntent.getBroadcast(
            context,
            4,
            rejectCallIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        // Style for popup notification
        val bigText = NotificationCompat.BigTextStyle()
        bigText.setBigContentTitle("$dialerName calling..")
        //bigText.bigText(campaignName)

        // Create notification
        val builder = NotificationCompat.Builder(context, CHANEL_CALL_ID)
        builder.setSmallIcon(R.drawable.ic_notif_pick_up_audio)
            .setColor(-0xff0100)
            .setAutoCancel(false)
            .setContentTitle(title)
            .setContentIntent(pendingIntent)
            .setContentText("$dialerName calling..")
            .setSubText(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) title else "Incoming ${if (isVideoCall) "Video Call" else "Call"}")
            .setDefaults(Notification.DEFAULT_ALL)
            .setStyle(bigText)
            .setDeleteIntent(pendingRejectCall)
            .setOngoing(true)
            .setSound(null)
            .setPriority(Notification.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .addAction(R.drawable.ic_notif_cancel_call, "Reject", pendingRejectCall)

        builder.addAction(
            if (isVideoCall) {
                R.drawable.ic_notif_pick_up_video
            } else {
                R.drawable.ic_notif_pick_up_audio
            }, "Answer", pendingIntent
        )

        val mNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = builder.build()
        notification.flags = FLAG_NO_CLEAR
        mNotificationManager.notify(notificationId, notification)
    }

    companion object {
        const val CHANEL_CALL_ID = "abto_phone_call"
        private var channelCall: NotificationChannel? = null
        const val KEY_PICK_UP_AUDIO = "KEY_PICK_UP_AUDIO"
        const val KEY_PICK_UP_VIDEO = "KEY_PICK_UP_VIDEO"
        const val KEY_REJECT_CALL = "KEY_REJECT_CALL"
        private const val NOTIFICATION_INCOMING_CALL_ID = 1000
        fun cancelIncCallNotification(context: Context, callId: Int) {
            val mNotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.cancel(NOTIFICATION_INCOMING_CALL_ID + callId)
        }
    }
}