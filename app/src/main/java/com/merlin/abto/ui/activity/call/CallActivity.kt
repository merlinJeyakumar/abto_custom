package com.merlin.abto.ui.activity.call

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.merlin.abto.core.AppController
import com.merlin.abto.R
import com.merlin.abto.databinding.LayoutCallBinding
import com.merlin.abto.extension.obtainViewModel
import com.support.baseApp.mvvm.MBaseActivity
import com.vanniktech.rxpermission.Permission
import com.vanniktech.rxpermission.RealRxPermission
import kotlinx.android.synthetic.main.layout_call.*
import org.abtollc.sdk.AbtoPhone
import org.jetbrains.anko.toast

class CallActivity : MBaseActivity<LayoutCallBinding, CallViewModel>() {

    private var TAG: String = CallActivity::class.java.simpleName
    private var intentIsIncomingCall: Boolean = false
    private var intentIsVideoCall: Boolean = false
    private var intentSipId: String? = ""
    private var intentCallId: Int = -1
    private var intentIsAttendedCall: Boolean = false

    companion object {
        var IS_INCOMING_CALL = "IS_INCOMING_CALL"
        var IS_VIDEO_CALL = "IS_VIDEO_CALL"
        var RECEIVER_SIP_ID = "RECEIVER_SIP_ID"
        var IS_ATTENDED_CALL = "IS_ATTENDED_CALL"
        var INCOMING_CALL_NOTIFICATION = "INCOMING_CALL_NOTIFICATION"
        var REQUEST_CODE_AUDIO_CONVERSATION = 224

        fun startActivity(
            mInstance: Activity,
            receiverSipId: String,
            isVideoCall: Boolean = false
        ) {
            mInstance.startActivityForResult(
                Intent(
                    mInstance,
                    CallActivity::class.java
                ).putExtra(
                    RECEIVER_SIP_ID,
                    receiverSipId
                ).putExtra(
                    IS_INCOMING_CALL,
                    false
                ).putExtra(
                    IS_VIDEO_CALL,
                    isVideoCall
                ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP),
                REQUEST_CODE_AUDIO_CONVERSATION
            )
        }
    }

    override fun initializeViewModel(): CallViewModel {
        return obtainViewModel(CallViewModel::class.java).apply {
            binding.viewmodel = this
        }
    }

    override fun getBaseLayoutId(): Int {
        return R.layout.layout_call
    }

    override fun setUpChildUI(savedInstanceState: Bundle?) {
        viewModel.subscribe()

        initData()
        initPermission()
        setupObserver()
    }

    private fun initPermission() {
        addRxCall(
            RealRxPermission.getInstance(AppController.instance)
                .requestEach(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA
                ).all {
                    it.state() == Permission.State.GRANTED
                }
                .subscribe({
                    if (it) {
                        initCall()
                    } else {
                        toast("Record, Camera Permission required")
                        viewModel.doHangup()
                    }
                }, {
                    it.printStackTrace()
                })
        )
    }

    private fun initData() {
        intentSipId = intent.extras?.getString(RECEIVER_SIP_ID, "")
        intentIsVideoCall = intent.extras?.getBoolean(IS_VIDEO_CALL, false)!! //FIXME: INCOMING CALL LABEL NOT WORKING
        intentIsIncomingCall = intent.extras?.getBoolean(IS_INCOMING_CALL, false)!!
        intentCallId = intent?.getIntExtra(AbtoPhone.CALL_ID, 0)!!
        intentIsAttendedCall = intent?.getBooleanExtra(IS_ATTENDED_CALL, false)!!
    }

    private fun setupObserver() {
        viewModel.callConnectDisconnect.observe(this, Observer {
            if (it) {
                if (intentIsVideoCall) viewModel.setVideoCall(svLocalVideo, svRemoteView)
            } else finish()
        })
    }

    private fun initCall() {
        viewModel.setCall(
            receiverId = intentSipId!!,
            intentIsIncomingCall = intentIsIncomingCall,
            intentIsAttendedCall = intentIsAttendedCall,
            incomingCallId = intentCallId,
            intentIsVideoCall = intentIsVideoCall
        )
    }

    override fun onNetworkStatusChanged(isConnected: Boolean) {
    }

    override fun onBackPressed() {
        viewModel.doHangup()
    }
}
