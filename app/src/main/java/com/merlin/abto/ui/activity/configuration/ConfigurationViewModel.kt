package com.merlin.abto.ui.activity.configuration

import android.Manifest
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.data.repositories.AppSettingsRepository
import com.domain.models.CurrentUserSipModel
import com.merlin.abto.AppController
import com.merlin.abto.abto.AbtoHelper
import com.merlin.abto.abto.rxjava.AbtoRxEvents
import com.merlin.abto.abto.rxjava.AbtoState
import com.merlin.abto.ui.activity.main.MainViewModel
import com.support.baseApp.mvvm.MBaseViewModel
import com.support.rxJava.RxBus
import com.support.rxJava.Scheduler.ui
import com.support.utills.Log
import com.vanniktech.rxpermission.Permission
import com.vanniktech.rxpermission.RealRxPermission
import org.abtollc.sdk.AbtoPhoneCfg

class ConfigurationViewModel(
    private val appSettingsRepository: AppSettingsRepository,
    private val abtoHelper: AbtoHelper
) : MBaseViewModel(AppController.instance) {

    private val TAG: String = MainViewModel::class.java.simpleName

    private val _sipConnectStatusText = MutableLiveData<String>()
    val sipConnectStatusText: LiveData<String>
        get() = _sipConnectStatusText

    private val _signallingTransport = MutableLiveData<String>()
    val signallingTransport: MutableLiveData<String>
        get() = _signallingTransport

    private val _keepAliveTransportInterval = MutableLiveData<String>()
    val keepAliveTransportInterval: LiveData<String>
        get() = _keepAliveTransportInterval

    private val _sipPort = MutableLiveData<String>()
    val sipPort: MutableLiveData<String>
        get() = _sipPort

    private val _rtpPort = MutableLiveData<String>()
    val rtpPort: MutableLiveData<String>
        get() = _rtpPort

    private val _videoQuality = MutableLiveData<String>()
    val videoQuality: MutableLiveData<String>
        get() = _videoQuality

    private val _autoSendRtpVideo = MutableLiveData<Boolean>()
    val autoSendRtpVideo: MutableLiveData<Boolean>
        get() = _autoSendRtpVideo

    private val _autoSendRtpAudio = MutableLiveData<Boolean>()
    val autoSendRtpAudio: MutableLiveData<Boolean>
        get() = _autoSendRtpAudio

    private val _sipConnectionStatus = MutableLiveData<Boolean>()
    val sipConnectionStatus: LiveData<Boolean>
        get() = _sipConnectionStatus

    private val _sipInitializeButton = MutableLiveData<String>()
    val sipInitializeButton: LiveData<String>
        get() = _sipInitializeButton

    private val _unInitializeLabelVisibility = MutableLiveData<Int>()
    val unInitializeLabelVisibility: LiveData<Int>
        get() = _unInitializeLabelVisibility

    private val _registerTimeout = MutableLiveData<String>()
    val registerTimeout: LiveData<String>
        get() = _registerTimeout

    private val _hangupTimeout = MutableLiveData<String>()
    val hangupTimeout: LiveData<String>
        get() = _hangupTimeout

    private val _verifyTlsServer = MutableLiveData<String>()
    val verifyTlsServer: LiveData<String>
        get() = _verifyTlsServer

    override fun subscribe() {
        initPermission()
        setupObserver()
        initUi()
    }

    private fun initPermission() {
        addRxCall(
            RealRxPermission.getInstance(AppController.instance)
                .requestEach(
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.USE_SIP
                ).all {
                    it.state() == Permission.State.GRANTED
                }
                .subscribe({
                    if (it) {
                        abtoHelper.initializeAbto()
                    } else {
                        _sipConnectStatusText.value = "Not Connected, Phone Permission required"
                        toastMessage.value = "Phone Permission required"
                    }
                }, {
                    it.printStackTrace()
                })
        )
    }

    private fun setupObserver() {
        addRxCall(
            RxBus.listen(AbtoRxEvents.AbtoConnectionChanged::class.java)
                .subscribeOn(ui())
                .observeOn(ui())
                .subscribe {
                    when (it.abtoState) {
                        AbtoState.INITIALIZING -> {
                            _sipConnectStatusText.value = "initializing"
                            Log.e(TAG, "setupObserver initializing")
                        }
                        AbtoState.INITIALIZED -> {
                            _sipConnectStatusText.value = "initialized"
                            onInitializeCreatedDestroyed(true)
                            Log.e(TAG, "setupObserver initialized")
                        }
                        AbtoState.INITIALIZATION_FAILED -> {
                            showProgress.value = false
                            it.throwable?.printStackTrace()
                            _sipConnectStatusText.value =
                                "initializing failed ${it.throwable?.localizedMessage}"
                            Log.e(TAG, "setupObserver initializing failed")
                        }
                        AbtoState.REGISTERING -> {
                            _sipConnectStatusText.value = "Registering"
                            Log.e(TAG, "setupObserver Registering")
                        }
                        AbtoState.REGISTERED -> {
                            showProgress.value = false
                            _sipConnectStatusText.value = "Registered"
                            Log.e(TAG, "setupObserver Registered")
                            _sipInitializeButton.postValue("UnInitialize")
                        }
                        AbtoState.REGISTERING_FAILED -> {
                            showProgress.value = false
                            it.throwable?.printStackTrace()
                            _sipConnectStatusText.value =
                                "registering failed ${it.throwable?.localizedMessage}"
                        }
                        AbtoState.UNREGISTERED -> {
                            _sipConnectStatusText.value = "unregistered"
                            Log.e(TAG, "setupObserver UNREGISTERED")
                        }
                        AbtoState.DESTROYED -> {
                            onInitializeCreatedDestroyed(false)
                            Log.e(TAG, "setupObserver Destroyed")
                        }
                    }
                })
    }

    private fun onInitializeCreatedDestroyed(isInitialized: Boolean) {
        _sipConnectionStatus.postValue(isInitialized)
        if (isInitialized) {
            _sipInitializeButton.postValue("UnInitialize & Configure")
            _unInitializeLabelVisibility.postValue(View.VISIBLE)
        } else {
            _unInitializeLabelVisibility.postValue(View.GONE)
            _sipConnectStatusText.value = "service destroyed"
            _sipInitializeButton.postValue("Save & Initialize")
        }
    }

    private fun initUi() {
        _sipConnectionStatus.postValue(abtoHelper.isAbtoInitialized())
        _sipConnectStatusText.postValue(if (abtoHelper.isAbtoInitialized()) "Registered" else "Registering")
        _sipInitializeButton.postValue(if (abtoHelper.isAbtoInitialized()) "UnInitialize" else "Save & Initialize")
        _unInitializeLabelVisibility.postValue(if (abtoHelper.isAbtoInitialized()) View.VISIBLE else View.GONE)
        _signallingTransport.postValue(
            AbtoPhoneCfg.SignalingTransportType.getTypeByValue(
                appSettingsRepository.getCurrentUserSipModel().signalingTransportType
            ).name
        )
        _keepAliveTransportInterval.postValue("${appSettingsRepository.getCurrentUserSipModel().keepAliveInterval}")
        _sipPort.postValue("${appSettingsRepository.getCurrentUserSipModel().sipPort}")
        _rtpPort.postValue("${appSettingsRepository.getCurrentUserSipModel().rtpPort}")
        _videoQuality.postValue(appSettingsRepository.getCurrentUserSipModel().videoQuality)
        _autoSendRtpAudio.postValue(appSettingsRepository.getCurrentUserSipModel().autoSendRtpAudio)
        _autoSendRtpVideo.postValue(appSettingsRepository.getCurrentUserSipModel().autoSendRtpVideo)
        _registerTimeout.postValue(appSettingsRepository.getCurrentUserSipModel().registerTimeout.toString())
        _hangupTimeout.postValue(appSettingsRepository.getCurrentUserSipModel().hangupTimeout.toString())
        _verifyTlsServer.postValue(appSettingsRepository.getCurrentUserSipModel().verifyTLSServer.toString())
    }

    fun getCurrentSipModel(): CurrentUserSipModel {
        return appSettingsRepository.getCurrentUserSipModel()
    }

    fun setSignalingTransport(signalingTransportType: Int) {
        appSettingsRepository.putCurrentUserSipModel(
            appSettingsRepository.getCurrentUserSipModel().apply {
                this.signalingTransportType = signalingTransportType
            })
        initUi()
    }

    fun setKeepAliveInterval(keepAliveInterval: Int = 30) {
        appSettingsRepository.putCurrentUserSipModel(
            appSettingsRepository.getCurrentUserSipModel().apply {
                this.keepAliveInterval = keepAliveInterval
            })
        initUi()
    }

    fun setSipPort(sipPort: Int = 0) {
        appSettingsRepository.putCurrentUserSipModel(
            appSettingsRepository.getCurrentUserSipModel().apply {
                this.sipPort = sipPort
            })
        initUi()
    }

    fun setRtpPort(rtpPort: Int = 0) {
        appSettingsRepository.putCurrentUserSipModel(
            appSettingsRepository.getCurrentUserSipModel().apply {
                this.rtpPort = rtpPort
            })
        initUi()
    }

    fun setVideoQuality(
        videoMode: String
    ) {
        appSettingsRepository.putCurrentUserSipModel(
            appSettingsRepository.getCurrentUserSipModel().apply {
                this.videoQuality = videoMode
            })
        initUi()
    }

    fun setAutoSendRtpAudio(isEnabled: Boolean = false) {
        appSettingsRepository.putCurrentUserSipModel(
            appSettingsRepository.getCurrentUserSipModel().apply {
                this.autoSendRtpAudio = isEnabled
            })
        initUi()
    }

    fun setAutoSendRtpVideo(isEnabled: Boolean = false) {
        appSettingsRepository.putCurrentUserSipModel(
            appSettingsRepository.getCurrentUserSipModel().apply {
                this.autoSendRtpVideo = isEnabled
            })
        initUi()
    }

    fun setRegisterTimeout(registerTimeout: Int) {
        appSettingsRepository.putCurrentUserSipModel(
            appSettingsRepository.getCurrentUserSipModel().apply {
                this.registerTimeout = registerTimeout
            })
        initUi()
    }

    fun setHangupTimeout(hangupTimeout: Int) {
        appSettingsRepository.putCurrentUserSipModel(
            appSettingsRepository.getCurrentUserSipModel().apply {
                this.hangupTimeout = hangupTimeout
            })
        initUi()
    }

    fun setVerifyTlsServer(verifyTlsServer: Boolean) {
        appSettingsRepository.putCurrentUserSipModel(
            appSettingsRepository.getCurrentUserSipModel().apply {
                this.verifyTLSServer = verifyTlsServer
            })
        initUi()
    }

    fun destroyAbtoService() {
        if (abtoHelper.isAbtoInitialized()) {
            addRxCall(abtoHelper.destroyAbtoService().subscribe({}, {
                toastMessage.postValue(it.localizedMessage)
                it.printStackTrace()
            }))
        } else {
            abtoHelper.initializeAbto(isForeground = true)

        }
    }
}