package com.merlin.abto.ui.activity.call

import android.graphics.drawable.Drawable
import android.view.SurfaceView
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.data.repositories.AppSettingsRepository
import com.merlin.abto.core.AppController
import com.merlin.abto.R
import com.merlin.abto.abto.AbtoHelper
import com.merlin.abto.abto.rxjava.AbtoRxEvents
import com.merlin.abto.abto.rxjava.CallState
import com.merlin.abto.abto.rxjava.RemoteAlert
import com.mithrai.supportmodule.extension.formatTimerMillis
import com.support.baseApp.mvvm.MBaseViewModel
import com.support.rxJava.RxBus
import com.support.rxJava.Scheduler
import com.support.utills.Log
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.concurrent.TimeUnit

class CallViewModel(
    val appSettingsRepository: AppSettingsRepository,
    val abtoHelper: AbtoHelper
) : MBaseViewModel(AppController.instance) {

    private var currentScreenId: Int = 0
    private var activeCallId: Int = -1
    private var activeCallState: CallState = CallState.CONNECTING

    var INCOMING_CALL_SCREEN = 1
    var ON_CALL_SCREEN = 2

    private var isVideoCall: Boolean = false
    private var isRecording: Boolean = false
    private var isSpeakerMode: Boolean = false
    private var isMicMute: Boolean = false
    private var countDownTimer: Long = 0
    private var countDownTimerDisposable: Disposable? = null
    private val TAG: String = CallViewModel::class.java.simpleName

    private val _onCallDisplayName = MutableLiveData<String>()
    val onCallDisplayName: LiveData<String>
        get() = _onCallDisplayName

    private val _connectionStatus = MutableLiveData<String>()
    val connectionStatus: LiveData<String>
        get() = _connectionStatus

    private val _callConnectDisconnect = MutableLiveData<Boolean>()
    val callConnectDisconnect: LiveData<Boolean>
        get() = _callConnectDisconnect

    private val _speakerModeText = MutableLiveData<String>()
    val speakerModeText: LiveData<String>
        get() = _speakerModeText

    private val _callSpeakerDrawable = MutableLiveData<Drawable>()
    val callSpeakerDrawable: LiveData<Drawable>
        get() = _callSpeakerDrawable

    private val _micMuteText = MutableLiveData<String>()
    val micMuteText: LiveData<String>
        get() = _micMuteText

    private val _micDrawable = MutableLiveData<Drawable>()
    val callMicDrawable: LiveData<Drawable>
        get() = _micDrawable

    private val _recordingDrawable = MutableLiveData<Drawable>()
    val recordingDrawable: LiveData<Drawable>
        get() = _recordingDrawable

    private val _recordingText = MutableLiveData<String>()
    val recordingText: LiveData<String>
        get() = _recordingText

    private val _endButtonBackground = MutableLiveData<Drawable>()
    val endButtonBackground: LiveData<Drawable>
        get() = _endButtonBackground

    private val _endButtonDrawable = MutableLiveData<Drawable>()
    val endButtonDrawable: LiveData<Drawable>
        get() = _endButtonDrawable

    private val _endButtonText = MutableLiveData<String>()
    val endButtonText: LiveData<String>
        get() = _endButtonText

    private val _incomingCallLayoutVisibility = MutableLiveData<Int>()
    val incomingCallLayoutVisibility: LiveData<Int>
        get() = _incomingCallLayoutVisibility

    private val _ongoingCallLayoutVisibility = MutableLiveData<Int>()
    val ongoingCallLayoutVisibility: LiveData<Int>
        get() = _ongoingCallLayoutVisibility

    private val _onCallOptions = MutableLiveData<Int>()
    val onCallOptions: LiveData<Int>
        get() = _onCallOptions

    override fun subscribe() {
        initUi()
    }

    private fun initUi() {
        _callSpeakerDrawable.postValue(getContext().resources.getDrawable(R.drawable.speaker_off))
        _speakerModeText.postValue("Speaker off")

        _recordingDrawable.postValue(getContext().resources.getDrawable(R.drawable.ic_record_on))
        _recordingText.postValue("Recording")

        _micDrawable.postValue(getContext().resources.getDrawable(R.drawable.mic_on))
        _micMuteText.postValue("Mic on")

        _endButtonBackground.postValue(getContext().resources.getDrawable(R.drawable.circle_drawable_red))
        _endButtonDrawable.postValue(getContext().resources.getDrawable(R.drawable.call_end))
        _endButtonText.postValue("End Call")
    }

    fun setCall(
        receiverId: String,
        intentIsIncomingCall: Boolean,
        intentIsAttendedCall: Boolean = false,
        incomingCallId: Int = -1,
        intentIsVideoCall: Boolean
    ) {

        activeCallId = incomingCallId
        isVideoCall = intentIsVideoCall
        _onCallDisplayName.postValue(receiverId)

        initObserver()

        if (!intentIsIncomingCall) {
            switchScreen(ON_CALL_SCREEN)
            startCall(receiverId, intentIsVideoCall)

        } else if (intentIsAttendedCall && intentIsIncomingCall) {
            doAnswer()

        } else if (intentIsIncomingCall) {
            _connectionStatus.postValue(
                "Incoming ${if (!intentIsVideoCall) "Video" else ""}Call"
            )
            switchScreen(INCOMING_CALL_SCREEN)
        }
    }

    private fun startCall(receiverId: String, isVideoCall: Boolean) {
        addRxCall(abtoHelper.dialCall(receiverId, isVideoCall).subscribe({
            activeCallId = it
        }, {
            toastMessage.value = "error ${it.localizedMessage}"
            it.printStackTrace()
        }))
    }

    private fun initObserver() {
        addRxCall(
            RxBus.listen(AbtoRxEvents.CallConnectionChanged::class.java)
                .subscribeOn(Scheduler.ui())
                .observeOn(Scheduler.ui())
                .subscribe {
                    activeCallState = it.state
                    when (it.state) {
                        CallState.CONNECTING -> {
                            _connectionStatus.postValue("Connecting..")
                            Log.e(TAG, "setupObserver CONNECTING")
                        }
                        CallState.CONNECTED -> {
                            activeCallId = it.callId.toInt()
                            _callConnectDisconnect.postValue(true)
                            Log.e(TAG, "setupObserver CONNECTED ${it.callId}")
                            setRecording()
                            showTimer()
                            showCallMenu()
                            switchScreen(ON_CALL_SCREEN)
                        }
                        CallState.LOCAL_HOLD -> {
                            activeCallId = it.callId.toInt()
                            _connectionStatus.postValue("Call on Hold")
                            Log.e(TAG, "setupObserver LOCAL_HOLD ${it.callId}")
                        }
                        CallState.REMOTE_HOLD -> {
                            activeCallId = it.callId.toInt()
                            _connectionStatus.postValue("Call on Hold - Remote")
                            Log.e(TAG, "setupObserver REMOTE_HOLD ${it.callId}")
                        }
                        CallState.DISCONNECTED -> {
                            activeCallId = it.callId.toInt()
                            _connectionStatus.postValue("Call Disconnected - ${it.message}")
                            Log.e(TAG, "setupObserver DISCONNECTED ${it.callId}")
                            _callConnectDisconnect.postValue(false)
                            showDisconnected()
                        }
                        CallState.ERROR -> {
                            activeCallId = it.callId.toInt()
                            _connectionStatus.postValue("Call on error ${it.errorCode}")
                            Log.e(TAG, "setupObserver ERROR ${it.callId}")
                            showDisconnected()
                        }
                        CallState.ACTIVE -> {
                            Log.e(TAG, "setupObserver ACTIVE ${it.callId}")
                        }
                    }
                })

        addRxCall(RxBus.listen(AbtoRxEvents.OnRemoteAlert::class.java).subscribeOn(Scheduler.ui())
            .observeOn(Scheduler.ui())
            .subscribe {
                activeCallId = it.callId
                when (it.alert) {
                    RemoteAlert.TRYING -> {
                        _connectionStatus.postValue("Connecting..")
                    }
                    RemoteAlert.RINGING -> {
                        _connectionStatus.postValue("Ringing..")
                    }
                    RemoteAlert.SESSION_PROGRESS -> {
                    }
                    RemoteAlert.UNKNOWN -> {
                        _connectionStatus.postValue("Unknown?")
                    }
                }
            })
    }

    private fun showCallMenu() {
        _onCallOptions.postValue(View.VISIBLE)
    }

    private fun showTimer() {
        countDownTimerDisposable?.dispose()
        countDownTimerDisposable = null
        countDownTimerDisposable = Single.create<Long> {
            countDownTimer++
            it.onSuccess(countDownTimer)
        }
            .delay(1, TimeUnit.SECONDS)
            .repeat()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                _connectionStatus.postValue(TimeUnit.SECONDS.toMillis(it).formatTimerMillis())
            }
    }

    fun doAnswer() {
        addRxCall(
            abtoHelper.answerCall(activeCallId, isVideoCall)
                .subscribe({
                    switchScreen(ON_CALL_SCREEN)
                }, {
                    toastMessage.value = it.localizedMessage
                    _callConnectDisconnect.postValue(false)
                })
        )
    }

    fun doHangup() {
        addRxCall(abtoHelper.hangupCall(activeCallId).subscribe({}, {
            it.printStackTrace()
            Log.e(TAG, it.localizedMessage)
        }))
    }

    fun setSpeakerMode() {
        abtoHelper.setSpeakerMode(activeCallId, !isSpeakerMode)
        isSpeakerMode = !isSpeakerMode

        _callSpeakerDrawable.postValue(
            when (isSpeakerMode) {
                true -> getContext().resources.getDrawable(R.drawable.speaker_on)
                false -> getContext().resources.getDrawable(R.drawable.speaker_off)
            }
        )

        _speakerModeText.postValue(
            when (isSpeakerMode) {
                true -> "Speaker on"
                false -> "Speaker off"
            }
        )
    }

    fun setMicMute() {
        abtoHelper.setMicMute(activeCallId, !isMicMute)
        _micDrawable.postValue(
            when (!isMicMute) {
                false -> getContext().resources.getDrawable(R.drawable.mic_on)
                true -> getContext().resources.getDrawable(R.drawable.mic_off)
            }
        )
        _micMuteText.postValue(
            when (!isMicMute) {
                true -> "Mic on"
                false -> "Mic off"
            }
        )
        isMicMute = !isMicMute
    }

    fun setRecording() {
        if (!isRecording) {
            addRxCall(abtoHelper.startRecording(activeCallId, File("")).subscribe({
                _recordingDrawable.postValue(getContext().resources.getDrawable(R.drawable.ic_record_on))
                isRecording = true
            }, {
                _recordingDrawable.postValue(getContext().resources.getDrawable(R.drawable.ic_record_off))
                isRecording = false
            }))
        } else {
            _recordingDrawable.postValue(getContext().resources.getDrawable(R.drawable.ic_record_off))
            abtoHelper.stopRecording(activeCallId)
            isRecording = false
        }

        _recordingText.value = when (isRecording) {
            true -> "Recording"
            false -> "Record"
        }
    }

    private fun showDisconnected() {
        _endButtonBackground.postValue(getContext().resources.getDrawable(R.drawable.circle_drawable_grey))
        _endButtonDrawable.postValue(getContext().resources.getDrawable(R.drawable.ic_baseline_close_35))
        _endButtonText.postValue("Close")
    }

    private fun switchScreen(screenId: Int = INCOMING_CALL_SCREEN) {
        if (currentScreenId == screenId) {
            return
        }
        if (screenId == INCOMING_CALL_SCREEN) {
            _ongoingCallLayoutVisibility.postValue(View.GONE)
            _incomingCallLayoutVisibility.postValue(View.VISIBLE)
        } else {
            _ongoingCallLayoutVisibility.postValue(View.VISIBLE)
            _incomingCallLayoutVisibility.postValue(View.GONE)
        }
        _onCallOptions.postValue(View.GONE)
        currentScreenId = screenId
    }

    fun setVideoCall(svLocalVideo: SurfaceView?, svRemoteView: SurfaceView?) {
        svLocalVideo?.visibility = View.VISIBLE
        svRemoteView?.visibility = View.VISIBLE
        abtoHelper.setVideoView(activeCallId, svLocalVideo!!, svRemoteView!!)
        svLocalVideo.setZOrderOnTop(true)
        svLocalVideo.setZOrderMediaOverlay(true)
    }
}