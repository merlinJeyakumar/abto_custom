package com.merlin.abto.ui.activity.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.merlin.abto.AppController
import com.merlin.abto.abto.AbtoHelper
import com.support.baseApp.mvvm.MBaseViewModel

class SplashViewModel(
    private val abtoHelper: AbtoHelper
) : MBaseViewModel(AppController.instance) {

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean>
        get() = _isLoggedIn

    override fun subscribe() {
        isAbtoRegistered()
    }

    private fun isAbtoRegistered() {
        _isLoggedIn.value = abtoHelper.isAbtoAccountAdded()
    }

}