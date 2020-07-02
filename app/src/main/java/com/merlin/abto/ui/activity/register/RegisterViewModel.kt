package com.merlin.abto.ui.activity.register

import android.Manifest
import androidx.lifecycle.MutableLiveData
import com.data.repositories.AppSettingsRepository
import com.merlin.abto.core.AppController
import com.merlin.abto.R
import com.merlin.abto.abto.AbtoHelper
import com.merlin.abto.abto.rxjava.AbtoRxEvents
import com.merlin.abto.abto.rxjava.AbtoState.*
import com.merlin.abto.abto.utility.getSipProps
import com.support.baseApp.mvvm.MBaseViewModel
import com.support.rxJava.RxBus
import com.support.rxJava.Scheduler.ui
import com.support.utills.Log
import com.vanniktech.rxpermission.Permission
import com.vanniktech.rxpermission.RealRxPermission

class RegisterViewModel(
    private val appSettingsRepository: AppSettingsRepository,
    private val abtoHelper: AbtoHelper
) : MBaseViewModel(AppController.instance) {

    private val TAG: String = RegisterViewModel::class.java.simpleName
    private val defaultSipId =
        "${appSettingsRepository.getCurrentUserSipModel().sipIdentity}@${appSettingsRepository.getCurrentUserSipModel().sipDomain};${appSettingsRepository.getCurrentUserSipModel().password}"

    private val _registerLiveData = MutableLiveData<Unit>()
    val registerLiveData: MutableLiveData<Unit>
        get() = _registerLiveData

    private val _sipId = MutableLiveData<String>()
    val sipId: MutableLiveData<String>
        get() = _sipId

    private val _sipPassword = MutableLiveData<String>()
    val sipPassword: MutableLiveData<String>
        get() = _sipPassword

    private val _sipDomain = MutableLiveData<String>()
    val sipDomain: MutableLiveData<String>
        get() = _sipDomain

    private val _sipConnectionStatus = MutableLiveData<String>()
    val sipConnectionStatus: MutableLiveData<String>
        get() = _sipConnectionStatus


    override fun subscribe() {
        prepareSipList()
        setupObserver()
        setUI(defaultSipId)
    }

    private fun setupObserver() {
        addRxCall(RxBus.listen(AbtoRxEvents.AbtoConnectionChanged::class.java)
            .subscribeOn(ui())
            .observeOn(ui())
            .subscribe {
                when (it.abtoState) {
                    INITIALIZING -> {
                        showProgress.value = true
                        sipConnectionStatus.value = "initializing"
                        Log.e(TAG, "setupObserver initializing")
                    }
                    INITIALIZED -> {
                        //TODO:
                        sipConnectionStatus.value = "initialized"
                        Log.e(TAG, "setupObserver initialized")
                    }
                    INITIALIZATION_FAILED -> {
                        showProgress.value = false
                        it.throwable?.printStackTrace()
                        sipConnectionStatus.value =
                            "initializing failed ${it.throwable?.localizedMessage}"
                        Log.e(TAG, "setupObserver initializing failed")
                    }
                    REGISTERING -> {
                        sipConnectionStatus.value = "Registering"
                        Log.e(TAG, "setupObserver Registering")
                    }
                    REGISTERED -> {
                        showProgress.value = false
                        sipConnectionStatus.value = "Registered"
                        Log.e(TAG, "setupObserver Registered")
                        _registerLiveData.value = Unit
                    }
                    REGISTERING_FAILED -> {
                        showProgress.value = false
                        it.throwable?.printStackTrace()
                        sipConnectionStatus.value =
                            "registering failed ${it.throwable?.localizedMessage}"
                    }
                    UNREGISTERED -> {
                        Log.e(TAG, "setupObserver UNREGISTERED3")
                        sipConnectionStatus.value = "unregistered"
                    }
                }
            })
    }

    private fun prepareSipList() {
        if (getSipList().isNullOrEmpty()) {
            updateSipList(getContext().resources.getStringArray(R.array.sip_array).toList())
        }
    }

    private fun addSipId(sip: String) {
        if (!getSipList().contains(sip)) {
            updateSipList(getSipList().toMutableList().apply {
                this.add(sip)
            })
        }
    }

    private fun updateSipList(list: List<String>) {
        appSettingsRepository.putSipList(list)
    }

    fun getSipList(): List<String> {
        return appSettingsRepository.getSipList()
    }

    fun setUI(defaultSipId: String) {
        val sipProps = getSipProps(defaultSipId)
        _sipId.value = sipProps[0]
        _sipDomain.value = sipProps[1]
        _sipPassword.value = sipProps[2]
    }

    fun doRegister(sipId: String, domainName: String, password: String) {
        if (sipId.length <= 2) {
            toastMessage.value = "sipId is not valid"
            return
        }
        if (domainName.length <= 2) {
            toastMessage.value = "domainName is not valid"
            return
        }
        if (password.length <= 2) {
            toastMessage.value = "password is not valid"
            return
        }
        addSipId("${sipId}@${domainName};${password}")
        appSettingsRepository.putCurrentUserSipModel(
            appSettingsRepository.getCurrentUserSipModel().apply {
                this.displayName = sipId
                this.sipIdentity = sipId
                this.sipDomain = domainName
                this.password = password
            })
        initializeAbtoRegistration()
    }

    private fun initializeAbtoRegistration() {
        addRxCall(
            RealRxPermission.getInstance(AppController.instance)
                .requestEach(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.USE_SIP,
                    Manifest.permission.READ_PHONE_STATE
                )
                .all {
                    it.state() == Permission.State.GRANTED
                }
                .subscribe({
                    if (it) {
                        abtoHelper.initializeAbto()
                    } else {
                        toastMessage.value = "Phone permission required for initializing SIP"
                    }
                }, {
                    it.printStackTrace()
                })
        )
    }
}