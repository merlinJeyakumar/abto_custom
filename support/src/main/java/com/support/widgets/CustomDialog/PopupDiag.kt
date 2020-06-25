package com.mithrai.crowdai.Abstract.View

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.PopupWindow

open class PopupDiag(val context: Context?) : PopupWindow(context) {


    override fun dismiss() {
        if (isActivityFinishing()) {
            return
        }
        super.dismiss()
    }

    override fun showAsDropDown(anchor: View?) {
        if (isActivityFinishing()) {
            return
        }
        super.showAsDropDown(anchor)
    }

    private fun isActivityFinishing(): Boolean {
        if (context is Activity) {
            context.let {
                val activity_ = it as Activity
                if (activity_.isFinishing) {
                    return false
                } else {
                    return false
                }
            }
        } else {
            return false
        }
    }
}