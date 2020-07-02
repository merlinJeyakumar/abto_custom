package com.merlin.abto.ui.activity.main

import android.Manifest
import android.widget.ArrayAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.data.repositories.AppSettingsRepository
import com.merlin.abto.core.AppController
import com.merlin.abto.abto.AbtoHelper
import com.merlin.abto.abto.rxjava.AbtoRxEvents
import com.merlin.abto.abto.rxjava.AbtoState
import com.support.baseApp.mvvm.MBaseViewModel
import com.support.rxJava.RxBus
import com.support.rxJava.Scheduler.ui
import com.support.utills.Log
import com.support.utills.shareFile
import com.vanniktech.rxpermission.Permission
import com.vanniktech.rxpermission.RealRxPermission
import io.reactivex.schedulers.Schedulers
import org.abtollc.sdk.AbtoPhoneCfg
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

class MainViewModel(
    private val appSettingsRepository: AppSettingsRepository,
    private val abtoHelper: AbtoHelper
) : MBaseViewModel(AppController.instance) {

    private val TAG: String = MainViewModel::class.java.simpleName
    private lateinit var arrayAdapter: ArrayAdapter<String>

    private val _registeredIdLiveData = MutableLiveData<String>()
    val registeredIdLiveData: MutableLiveData<String>
        get() = _registeredIdLiveData

    private val _sipTransportTypes = MutableLiveData<String>()
    val sipTransportTypes: MutableLiveData<String>
        get() = _sipTransportTypes

    private val _sipConnectionStatus = MutableLiveData<String>()
    val sipConnectionStatus: MutableLiveData<String>
        get() = _sipConnectionStatus

    private val _connectCall = MutableLiveData<Pair<String, Boolean>>()
    val connectCall: LiveData<Pair<String, Boolean>>
        get() = _connectCall

    private val _unregisterLiveData = MutableLiveData<Unit>()
    val unregisterLiveData: LiveData<Unit>
        get() = _unregisterLiveData

    private val _sipText = MutableLiveData<String>()
    val sipText: LiveData<String>
        get() = _sipText

    private val _connectionStatus = MutableLiveData<Boolean>()
    val connectionStatus: LiveData<Boolean>
        get() = _connectionStatus

    override fun subscribe() {
        initPermission()
        setupObserver()
        initAdapterAutoComplete()
        initUi()
    }

    fun initPermission() {
        addRxCall(
            RealRxPermission.getInstance(AppController.instance)
                .requestEach(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.USE_SIP
                ).all {
                    it.state() == Permission.State.GRANTED
                }
                .subscribe({ it ->
                    if (it) {
                        abtoHelper.initializeAbto()

                    } else {
                        sipConnectionStatus.value = "Not Connected, Phone Permission required"
                        toastMessage.value = "Phone Permission required"
                    }
                }, {
                    it.printStackTrace()
                })
        )
    }

    private fun initUi() {
        _registeredIdLiveData.postValue("${appSettingsRepository.getCurrentUserSipModel().sipIdentity}@${appSettingsRepository.getCurrentUserSipModel().sipDomain}")
        val signalingTransportType =
            if (appSettingsRepository.getCurrentUserSipModel().signalingTransportType == AbtoPhoneCfg.SignalingTransportType.TCP.value) {
                "TCP"
            } else {
                "UDP"
            }
        _sipTransportTypes.postValue("[ST: $signalingTransportType - AT: ${appSettingsRepository.getCurrentUserSipModel().keepAliveInterval}]")

        _sipText.postValue(appSettingsRepository.getDraftText())

        _sipConnectionStatus.postValue(if (abtoHelper.isAbtoRegistered()) "Registered" else "Registering")
    }

    private fun setupObserver() {
        addRxCall(
            RxBus.listen(AbtoRxEvents.AbtoConnectionChanged::class.java)
                .subscribeOn(ui())
                .observeOn(ui())
                .subscribe {
                    when (it.abtoState) {
                        AbtoState.INITIALIZING -> {
                            _connectionStatus.postValue(true)
                            _sipConnectionStatus.value = "initializing"
                            Log.e(TAG, "setupObserver initializing")
                        }
                        AbtoState.INITIALIZED -> {
                            _connectionStatus.postValue(true)
                            _sipConnectionStatus.value = "initialized"
                            Log.e(TAG, "setupObserver initialized")
                        }
                        AbtoState.INITIALIZATION_FAILED -> {
                            _connectionStatus.postValue(false)
                            showProgress.value = false
                            it.throwable?.printStackTrace()
                            _sipConnectionStatus.value =
                                "initializing failed ${it.throwable?.localizedMessage}"
                            Log.e(TAG, "setupObserver initializing failed")
                        }
                        AbtoState.REGISTERING -> {
                            _connectionStatus.postValue(true)
                            _sipConnectionStatus.value = "Registering"
                            Log.e(TAG, "setupObserver Registering")
                        }
                        AbtoState.REGISTERED -> {
                            _connectionStatus.postValue(true)
                            showProgress.value = false
                            _sipConnectionStatus.value = "Registered"
                            Log.e(TAG, "setupObserver Registered")
                        }
                        AbtoState.REGISTERING_FAILED -> {
                            _connectionStatus.postValue(false)
                            showProgress.value = false
                            it.throwable?.printStackTrace()
                            _sipConnectionStatus.value =
                                "registering failed ${it.throwable?.localizedMessage}"
                        }
                        AbtoState.UNREGISTERED -> {
                            _connectionStatus.postValue(false)
                            _sipConnectionStatus.value = "unregistered"
                            _unregisterLiveData.postValue(Unit)
                            Log.e(TAG, "setupObserver UNREGISTERED")
                        }
                        AbtoState.DESTROYED -> {
                            _connectionStatus.postValue(false)
                            _sipConnectionStatus.value = "service destroyed"
                            Log.e(TAG, "setupObserver Destroyed")
                        }
                    }
                })
    }

    fun initAdapterAutoComplete(): ArrayAdapter<String> {
        arrayAdapter = ArrayAdapter(
            getContext(),
            android.R.layout.simple_list_item_1,
            appSettingsRepository.getRecentDialledList()
        )
        return arrayAdapter
    }

    fun doCall(sipIdentity: String, isVideoCall: Boolean) {
        onTextChanged(sipIdentity)
        if (sipIdentity.length < 2) {
            toastMessage.value = "id cannot be empty"
            return
        }
        val sipAddress = getCompleteSipAddress(sipIdentity)
        Log.e(TAG, "SipAddress $sipAddress")

        if (abtoHelper.isAbtoRegistered()) {
            _connectCall.value = Pair(sipAddress, isVideoCall)
        } else {
            toastMessage.value = "abto registration not available"
        }
    }

    private fun getCompleteSipAddress(sipIdentity: String): String {
        val buildString = StringBuffer()
        buildString.append(sipIdentity)

        if (!sipIdentity.contains("@")) {
            buildString.append("@")
            buildString.append(appSettingsRepository.getCurrentUserSipModel().sipDomain)
        }
        addSipToCompletionList(buildString.toString())
        try {
            buildString.append("?")
            buildString.append("header1=")
            buildString.append(URLEncoder.encode("qq<q>q", "UTF-8")) //value of 'header1'
            buildString.append("&")
            buildString.append("header2=")
            buildString.append(URLEncoder.encode("a@b", "UTF-8")) //value of 'header2'
        } catch (e: UnsupportedEncodingException) {
        }
        return buildString.toString()
    }

    private fun addSipToCompletionList(sipIdentity: String) {
        if (!appSettingsRepository.getRecentDialledList()
                .contains(sipIdentity)
        ) {
            appSettingsRepository.putRecentDialledList(
                appSettingsRepository.getRecentDialledList().toMutableList().apply {
                    this.add(sipIdentity)
                })
            arrayAdapter.add(sipIdentity)
            arrayAdapter.notifyDataSetChanged()
        }
    }

    fun doSendMessage(sipIdentity: String, message: String) {
        onTextChanged(sipIdentity)

        if (sipIdentity.length < 2) {
            toastMessage.value = "id cannot be empty"
            return
        }
        val sipAddress = getCompleteSipAddress(sipIdentity)
        Log.e(TAG, "SipAddress $sipAddress")

        if (abtoHelper.isAbtoRegistered()) {
            abtoHelper.sendMessage(sipIdentity, message)
        } else {
            toastMessage.value = "abto registration not available"
        }
    }

    private fun onTextChanged(text: String = "") {
        Log.i(TAG, "onTextChanged: text $text")
        appSettingsRepository.putDraftText(text)
    }

    fun unregister() {
        addRxCall(
            abtoHelper.unregisterAbto()
                .observeOn(ui())
                .subscribeOn(ui()).subscribe({}, {
                    toastMessage.value = it.localizedMessage
                })
        )
    }

    fun shareLog() {
        addRxCall(
            abtoHelper.getAbtoLogs()
                .subscribeOn(Schedulers.io())
                .observeOn(ui())
                .subscribe({
                    getContext().shareFile(it, "s-merlin@mithrai.com")
                }, {
//                    Log.e(TAG, it.localizedMessage)
                    it.printStackTrace()
                })
        )
    }

    fun clearLog() {
        abtoHelper.getAbtoLogsPath().deleteRecursively()
    }

    fun setSipAddress(it: String) {
        _sipText.postValue(it)
    }
}