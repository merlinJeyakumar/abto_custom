package com.merlin.abto.abto

import android.os.RemoteException
import android.view.SurfaceView
import com.data.repositories.AppSettingsRepository
import com.domain.datasources.IAbtoHandler
import com.merlin.abto.R
import com.merlin.abto.abto.rxjava.*
import com.merlin.abto.abto.utility.getForegroundNotification
import com.merlin.abto.abto.utility.getSipRemoteAddress
import com.merlin.abto.core.AppController
import com.merlin.abto.core.AppController.Companion.instance
import com.support.rxJava.RxBus
import com.support.utills.Log
import io.reactivex.Completable
import io.reactivex.Single
import org.abtollc.api.SipCallSession.StatusCode.*
import org.abtollc.sdk.*
import org.abtollc.utils.codec.Codec
import java.io.File

class AbtoHelper(private var appSettingsRepository: AppSettingsRepository) : IAbtoHandler {
    private val TAG: String = AbtoHelper::class.java.simpleName
    private var abtoPhone: AbtoPhone = instance.abtoPhone
    private var isDestroyed: Boolean = false
    private var retryTime: Int = 3
    private var registerRetries = 0 //NOOP - Success, AboveFailLimit
    private var activeCallMap = mutableMapOf<String, Int>()

    companion object {
        private var INSTANCE: AbtoHelper? = null
        fun getInstance(appSettingsDataSource: AppSettingsRepository): AbtoHelper {
            if (INSTANCE == null) {
                synchronized(AbtoHelper::javaClass) {
                    INSTANCE = AbtoHelper(appSettingsDataSource)
                }
            }
            return INSTANCE!!
        }
    }

    fun initializeAbto(
        sipDomain: String = appSettingsRepository.getCurrentUserSipModel().sipDomain,
        sipId: String = appSettingsRepository.getCurrentUserSipModel().sipIdentity,
        sipPassword: String = appSettingsRepository.getCurrentUserSipModel().password,
        sipDisplayName: String = appSettingsRepository.getCurrentUserSipModel().displayName,
        isForeground: Boolean = false,
        forceInitializing: Boolean = false
    ) {
        isDestroyed = false
        abtoPhone.setInMessageListener { message, senderSipId, currentSipId ->
            Log.i(TAG, "setInMessageListener $message $senderSipId $currentSipId")
            RxBus.publish(AbtoRxEvents.MessageReceived(message, senderSipId, currentSipId))
        }

        abtoPhone.setInitializeListener { initializeState: OnInitializeListener.InitializeState?, error: String? ->
            Log.i(TAG, "setInitializeListener ${initializeState?.name}")
            when (initializeState) {
                OnInitializeListener.InitializeState.START, OnInitializeListener.InitializeState.INFO, OnInitializeListener.InitializeState.WARNING -> {
                    Log.i(TAG, "initAbto: OnInitializeListener.InitializeState START INFO WARNING")
                    Log.e(TAG, "InitializeState START INFO WARNING $error")
                }
                OnInitializeListener.InitializeState.FAIL -> {
                    RxBus.publish(
                        AbtoRxEvents.AbtoConnectionChanged(
                            abtoState = AbtoState.INITIALIZATION_FAILED,
                            throwable = Exception(error).fillInStackTrace()
                        )
                    )
                    error(Exception(error).fillInStackTrace())
                }
                OnInitializeListener.InitializeState.SUCCESS -> {
                    Log.i(TAG, "initializeAbto OnInitializeListener.InitializeState.SUCCESS")
                    Log.i(TAG, "initializeAbto isAbtoRegistered ${isAbtoRegistered()}")
                    RxBus.publish(
                        AbtoRxEvents.AbtoConnectionChanged(
                            abtoState = AbtoState.INITIALIZED,
                            message = error!!
                        )
                    )
                    if (isAbtoRegistered()) {
                        RxBus.publish(
                            AbtoRxEvents.AbtoConnectionChanged(
                                abtoState = AbtoState.REGISTERED,
                                message = error
                            )
                        )
                        return@setInitializeListener
                    }
                    doAbtoRegister(sipDomain, sipId, sipPassword, sipDisplayName)
                }
            }

        }

        abtoPhone.setRegistrationStateListener(object : OnRegistrationListener {
            override fun onRegistered(accId: Long) {
                Log.i(
                    TAG,
                    "setRegistrationStateListener onRegistered $AppController.isSipRegistered $accId"
                )
                RxBus.publish(
                    AbtoRxEvents.AbtoConnectionChanged(
                        abtoState = AbtoState.REGISTERED,
                        errorCode = accId
                    )
                )
                registerRetries = 0
            }

            override fun onUnRegistered(accId: Long) {
                Log.i(
                    TAG,
                    "setRegistrationStateListener onUnRegistered $AppController.isSipRegistered $accId"
                )
                RxBus.publish(
                    AbtoRxEvents.AbtoConnectionChanged(
                        abtoState = AbtoState.UNREGISTERED
                    )
                )
                registerRetries = 0
            }

            override fun onRegistrationFailed(accId: Long, errorCode: Int, errorMessage: String) {
                Log.e(TAG, "SIP Initialization failed $errorMessage errorCode:$errorCode")
                RxBus.publish(
                    AbtoRxEvents.AbtoConnectionChanged(
                        abtoState = AbtoState.REGISTERING_FAILED,
                        errorCode = errorCode.toLong(),
                        message = errorMessage,
                        throwable = Exception(errorMessage)
                    )
                )
                if (!isForeground && !isDestroyed && registerRetries < retryTime) {
                    initializeAbto()
                    registerRetries++
                } else {
                    registerRetries = 0
                }
            }
        })

        abtoPhone.setCallConnectedListener { callId, remoteContact ->
            activeCallMap[getSipRemoteAddress(remoteContact)] = callId
            RxBus.publish(
                AbtoRxEvents.CallConnectionChanged(
                    state = CallState.CONNECTED,
                    callId = callId.toLong(),
                    errorCode = 200,
                    remoteContact = remoteContact
                )
            )
        }

        abtoPhone.setCallDisconnectedListener { callId, remoteContact, errorCode, errorMessage ->
            activeCallMap.remove(getSipRemoteAddress(remoteContact))
            RxBus.publish(
                AbtoRxEvents.CallConnectionChanged(
                    state = CallState.DISCONNECTED,
                    callId = callId.toLong(),
                    errorCode = errorCode.toLong(),
                    message = errorMessage,
                    remoteContact = remoteContact
                )
            )
        }

        abtoPhone.setOnCallHeldListener { callId, holdState ->
            val state: CallState = when (holdState) {
                OnCallHeldListener.HoldState.LOCAL_HOLD -> {
                    CallState.LOCAL_HOLD
                }
                OnCallHeldListener.HoldState.REMOTE_HOLD -> {
                    CallState.REMOTE_HOLD
                }
                OnCallHeldListener.HoldState.ACTIVE -> {
                    CallState.ACTIVE
                }
                OnCallHeldListener.HoldState.ERROR -> {
                    CallState.ERROR
                }
                else -> CallState.CONNECTING
            }
            RxBus.publish(
                AbtoRxEvents.CallConnectionChanged(
                    callId = callId.toLong(),
                    state = state
                )
            )
        }

        abtoPhone.setTextMessageStatusListener {
            when (it.status) {
                200 -> {
                    RxBus.publish(
                        AbtoRxEvents.MessageStatusChanged(
                            messageState = MessageState.SENT,
                            errorCode = it.status,
                            sipMessage = it
                        )
                    )
                }
                else -> {
                    RxBus.publish(
                        AbtoRxEvents.MessageStatusChanged(
                            messageState = MessageState.ERROR,
                            errorCode = it.status,
                            sipMessage = it
                        )
                    )
                }
            }
        }

        abtoPhone.setRemoteAlertingListener { callId, statusCode, accId ->
            RxBus.publish(
                AbtoRxEvents.OnRemoteAlert(
                    callId, when (statusCode) {
                        TRYING -> RemoteAlert.TRYING
                        RINGING -> RemoteAlert.RINGING
                        PROGRESS -> RemoteAlert.SESSION_PROGRESS
                        else -> RemoteAlert.UNKNOWN
                    }
                )
            )
        }

        if (forceInitializing || !isAbtoInitialized()) {
            initAbtoConfiguration()
            abtoPhone.initializeForeground(instance.getForegroundNotification())
            RxBus.publish(
                AbtoRxEvents.AbtoConnectionChanged(
                    AbtoState.INITIALIZING
                )
            )
        } else {
            doAbtoRegister(sipDomain, sipId, sipPassword, sipDisplayName)
        }
    }

    private fun doAbtoRegister(
        sipDomain: String,
        sipId: String,
        sipPassword: String,
        sipDisplayName: String
    ) {
        abtoPhone.config?.addAccount(
            sipDomain,
            null,
            sipId,
            sipPassword,
            null,
            sipDisplayName,
            300,
            false
        )
        try {
            abtoPhone.register()
            RxBus.publish(
                AbtoRxEvents.AbtoConnectionChanged(
                    abtoState = AbtoState.REGISTERING
                )
            )
            Log.i(TAG, "doAbtoRegister abtoPhone?.register()")
        } catch (e: RemoteException) {
            RxBus.publish(
                AbtoRxEvents.AbtoConnectionChanged(
                    abtoState = AbtoState.REGISTERING_FAILED,
                    throwable = e.fillInStackTrace()
                )
            )
            e.printStackTrace()
            Log.e(TAG, "doAbtoRegister abtoPhone?.register() ${e.localizedMessage}")
        }
    }

    private fun initAbtoConfiguration() {
        val config = abtoPhone.config
        for (c in Codec.values()) config.setCodecPriority(c, 0.toShort())
        config.setCodecPriority(Codec.G729, 81.toShort())
        config.setCodecPriority(Codec.PCMA, 80.toShort())
        config.setCodecPriority(Codec.PCMU, 79.toShort())
        config.setCodecPriority(Codec.H264, 220.toShort())
        config.setCodecPriority(Codec.H263_1998, 210.toShort())

        config.setSignallingTransport(
            AbtoPhoneCfg.SignalingTransportType.getTypeByValue(
                appSettingsRepository.getCurrentUserSipModel().signalingTransportType
            )
        )
        config.setKeepAliveInterval(
            AbtoPhoneCfg.SignalingTransportType.getTypeByValue(
                appSettingsRepository.getCurrentUserSipModel().signalingTransportType
            ), appSettingsRepository.getCurrentUserSipModel().keepAliveInterval
        )
        config.videoQualityMode =
            when (appSettingsRepository.getCurrentUserSipModel().videoQuality) {
                AbtoPhoneCfg.VIDEO_QUALITY_MODE.VIDEO_MODE_352_288.name -> AbtoPhoneCfg.VIDEO_QUALITY_MODE.VIDEO_MODE_352_288
                AbtoPhoneCfg.VIDEO_QUALITY_MODE.VIDEO_MODE_720_480.name -> AbtoPhoneCfg.VIDEO_QUALITY_MODE.VIDEO_MODE_720_480
                AbtoPhoneCfg.VIDEO_QUALITY_MODE.VIDEO_MODE_1280_720.name -> AbtoPhoneCfg.VIDEO_QUALITY_MODE.VIDEO_MODE_1280_720
                AbtoPhoneCfg.VIDEO_QUALITY_MODE.VIDEO_MODE_1920_1080.name -> AbtoPhoneCfg.VIDEO_QUALITY_MODE.VIDEO_MODE_1920_1080
                AbtoPhoneCfg.VIDEO_QUALITY_MODE.VIDEO_MODE_176_144.name -> AbtoPhoneCfg.VIDEO_QUALITY_MODE.VIDEO_MODE_176_144
                AbtoPhoneCfg.VIDEO_QUALITY_MODE.VIDEO_MODE_352_288_PORTRAIT.name -> AbtoPhoneCfg.VIDEO_QUALITY_MODE.VIDEO_MODE_352_288_PORTRAIT
                AbtoPhoneCfg.VIDEO_QUALITY_MODE.VIDEO_MODE_720_480_PORTRAIT.name -> AbtoPhoneCfg.VIDEO_QUALITY_MODE.VIDEO_MODE_720_480_PORTRAIT
                AbtoPhoneCfg.VIDEO_QUALITY_MODE.VIDEO_MODE_1280_720_PORTRAIT.name -> AbtoPhoneCfg.VIDEO_QUALITY_MODE.VIDEO_MODE_1280_720_PORTRAIT
                AbtoPhoneCfg.VIDEO_QUALITY_MODE.VIDEO_MODE_1920_1080_PORTRAIT.name -> AbtoPhoneCfg.VIDEO_QUALITY_MODE.VIDEO_MODE_1920_1080_PORTRAIT
                AbtoPhoneCfg.VIDEO_QUALITY_MODE.VIDEO_MODE_176_144_PORTRAIT.name -> AbtoPhoneCfg.VIDEO_QUALITY_MODE.VIDEO_MODE_176_144_PORTRAIT
                else -> AbtoPhoneCfg.VIDEO_QUALITY_MODE.VIDEO_MODE_DEFAULT
            }
        config.isTLSVerifyServer = appSettingsRepository.getCurrentUserSipModel().verifyTLSServer;
        config.registerTimeout = appSettingsRepository.getCurrentUserSipModel().registerTimeout
        config.hangupTimeout = appSettingsRepository.getCurrentUserSipModel().hangupTimeout
        config.inviteTimeout = appSettingsRepository.getCurrentUserSipModel().inviteTimeout
        config.dtmFmode = AbtoPhoneCfg.DTMF_MODE.INFO

        config.setEnableAutoSendRtpVideo(appSettingsRepository.getCurrentUserSipModel().autoSendRtpVideo)
        config.setEnableAutoSendRtpAudio(appSettingsRepository.getCurrentUserSipModel().autoSendRtpAudio)
        config.userAgent = this.abtoPhone.version()
        config.sipPort = appSettingsRepository.getCurrentUserSipModel().sipPort
        config.rtpPort = appSettingsRepository.getCurrentUserSipModel().rtpPort
        AbtoPhoneCfg.setLogLevel(7, true)
    }

    override fun sendMessage(receiverId: String, message: String): Completable {
        return Completable.create {
            if (abtoPhone.config.isConnectivityValid) {
                abtoPhone.sendTextMessage(abtoPhone.currentAccountId, message, receiverId)
                RxBus.publish(
                    AbtoRxEvents.MessageStatusChanged(
                        messageState = MessageState.SENDING
                    )
                )
            } else {
                error(instance.getString(R.string.internet_connection_is_not_valid))
            }
        }
    }

    override fun dialCall(receiverId: String, isVideoCall: Boolean): Single<Int> {
        return Single.create {
            if (abtoPhone.config.isConnectivityValid) {
                val callId: Int = if (isVideoCall) {
                    abtoPhone.startVideoCall(receiverId, abtoPhone.currentAccountId)
                } else {
                    abtoPhone.startCall(receiverId, abtoPhone.currentAccountId)
                }
                RxBus.publish(
                    AbtoRxEvents.CallConnectionChanged(
                        state = CallState.CONNECTING
                    )
                )
                activeCallMap[getSipRemoteAddress(receiverId)] = callId
                it.onSuccess(callId)
            } else {
                error(instance.getString(R.string.internet_connection_is_not_valid))
            }
        }
    }

    override fun hangupCall(callId: Int): Single<Unit> {
        return Single.create {
            if (abtoPhone.config.isConnectivityValid) {
                abtoPhone.hangUp(callId)
                it.onSuccess(Unit)
            } else {
                error(instance.getString(R.string.internet_connection_is_not_valid))
            }
        }
    }

    override fun holdCall(callId: Int): Completable {
        return Completable.create {
            if (abtoPhone.config.isConnectivityValid) {
                abtoPhone.holdRetriveCall(callId)
            } else {
                error(instance.getString(R.string.internet_connection_is_not_valid))
            }
        }
    }

    override fun answerCall(callId: Int, isVideoCall: Boolean): Completable {
        return Completable.create {
            if (abtoPhone.config.isConnectivityValid) {
                if (abtoPhone.isIncomingCall(callId)) {
                    abtoPhone.answerCall(callId, 200, isVideoCall)
                    it.onComplete()
                } else {
                    error("invalid call id")
                }
            } else {
                error(instance.getString(R.string.internet_connection_is_not_valid))
            }
        }
    }

    override fun setVideoView(
        activeCallId: Int,
        localVideoSurface: SurfaceView,
        remoteVideoSurface: SurfaceView
    ) {
        abtoPhone.setVideoWindows(activeCallId, localVideoSurface, remoteVideoSurface)
    }

    override fun setSpeakerMode(callId: Int, enable: Boolean) {
        abtoPhone.setSpeakerphoneOn(enable)
    }

    override fun setMicMute(callId: Int, enable: Boolean) {
        abtoPhone.setMicrophoneMute(enable)
    }

    override fun setVideoMute(callId: Int, enable: Boolean) {
        abtoPhone.muteLocalVideo(callId, enable)
    }

    override fun startRecording(callId: Int, audioFile: File): Completable {
        return Completable.create {
            if (abtoPhone.isActiveCall(callId)) {
                abtoPhone.startRecording(callId, audioFile.absolutePath)
            } else {
                error("inactive call")
            }
        }
    }

    override fun stopRecording(callId: Int) {
        abtoPhone.stopRecording(callId)
    }

    override fun restartAbto(): Completable {
        return Completable.create {
            if (abtoPhone.config.isConnectivityValid) {
                abtoPhone.restartSip()
            } else {
                error(instance.getString(R.string.internet_connection_is_not_valid))
            }
        }
    }

    override fun unregisterAbto(): Completable {
        return Completable.create {
            val isRegistered = isAbtoRegistered()
            if (isRegistered) {
                abtoPhone.unregister()
            }
            abtoPhone.config.getAccount(abtoPhone.currentAccountId).dbContentValues.clear()
            if (!isRegistered) {
                RxBus.publish(
                    AbtoRxEvents.AbtoConnectionChanged(
                        abtoState = AbtoState.UNREGISTERED
                    )
                )
                registerRetries = 0
            }
        }
    }

    override fun isAbtoRegistered(): Boolean {

        Log.i(TAG, "isAbtoRegistered")
        val acc = abtoPhone.currentAccountId
        if (acc == -1L || !abtoPhone.isActive) return false
        Log.i(TAG, "isAbtoRegistered acc == -1L || !abtoPhone?.isActive!!")
        try {
            val accState = abtoPhone.getSipProfileState(acc)
            if (accState != null && accState.isActive && accState.statusCode == 200) {
                Log.i(
                    TAG,
                    "isAbtoRegistered accState != null && accState.isActive && accState.statusCode == 200"
                )
                return true
            }
        } catch (e: Exception) {
            Log.e(TAG, "${e.localizedMessage}")
            e.printStackTrace()
        }
        return false
    }

    override fun isAbtoAccountAdded(): Boolean {
        return abtoPhone.currentAccountId.toInt() != -1 && (abtoPhone.config.getAccount(abtoPhone.currentAccountId).active)
    }

    override fun isAbtoInitialized(): Boolean {
        return abtoPhone.isActive
    }

    override fun isActiveCall(callId: Int): Boolean {
        return abtoPhone.isActiveCall(callId)
    }

    override fun isCallProcessing(): Boolean {
        return activeCallMap.isNotEmpty().also {
            if (it) {
                for (mutableEntry in activeCallMap) {
                    abtoPhone.hangUp(mutableEntry.value)
                }
            }
        }
    }

    fun getAbtoConfiguration(): AbtoPhoneCfg {
        return abtoPhone.config
    }

    override fun destroyAbtoService(): Completable {
        return Completable.create {
            abtoPhone.destroy()
            abtoPhone.isActive
            RxBus.publish(
                AbtoRxEvents.AbtoConnectionChanged(
                    abtoState = AbtoState.DESTROYED,
                    errorCode = 0,
                    message = "service destroyed",
                    throwable = Exception("service destroyed")
                )
            )
            isDestroyed = true
        }
    }

    override fun mergeCurrentCalls(): Completable {
        //NOOP
        return Completable.create {
            abtoPhone.joinAllCallsToConf()
        }
    }

}