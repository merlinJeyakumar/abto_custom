package com.mithrai.crowdai.Abstract.View

import android.app.Activity
import android.app.AlertDialog
import android.content.Context

open class AlertDiag(context: Context?) : AlertDialog(context) {

    override fun show() {
        if (isActivityFinishing()) {
            return
        }
        super.show()
    }

    override fun hide() {
        if (isActivityFinishing()) {
            return
        }
        super.hide()
    }

    override fun dismiss() {
        if (isActivityFinishing()) {
            return
        }
        super.dismiss()
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