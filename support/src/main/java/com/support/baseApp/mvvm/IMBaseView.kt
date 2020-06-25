package com.support.baseApp.mvvm

import android.view.View

interface IMBaseView {

    fun showProgress()

    fun showToastMessage(message: String)

    fun hideProgress()

    fun hideSoftKeyboard()

    fun showSoftKeyboard(view:View)

}
