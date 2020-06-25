package com.support.dialog

import android.app.Activity
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mithrai.crowdai.Abstract.View.ProgressDiag
import com.support.R
import com.support.utills.ViewUtils
import io.reactivex.Single


data class ProgressiveDialogModel(
    val materialAlertDialogBuilder: MaterialAlertDialogBuilder,
    val view: View,
    val progressBar: ProgressBar
) {
    fun setProgress(progress: Int) {
        this.progressBar.progress = progress
        this.view.findViewById<AppCompatTextView>(R.id.tvProgress).setText(progress)
        this.view.findViewById<AppCompatTextView>(R.id.tvProgressed).setText(progress)
    }
}

fun Activity.getConfirmationDialog(
    title: String = "Alert",
    message: String? = null,
    positiveText: String = this.getString(R.string.label_ok),
    negativeText: String = this.getString(R.string.label_cancel),
    isCancellable: Boolean = true
): Single<Boolean> {
    return Single.create { singleEmitter ->
        val materialAlertDialogBuilder = MaterialAlertDialogBuilder(this)
        materialAlertDialogBuilder.setTitle(title)
        materialAlertDialogBuilder.setCancelable(isCancellable)
        message?.let {
            materialAlertDialogBuilder.setMessage(it)
        }
        materialAlertDialogBuilder.setNegativeButton(negativeText) { dialog, which ->
            singleEmitter.onSuccess(false)
            materialAlertDialogBuilder.create().dismiss()
        }
        materialAlertDialogBuilder.setPositiveButton(positiveText) { dialog, which ->
            singleEmitter.onSuccess(
                true
            )
            materialAlertDialogBuilder.create().dismiss()
        }.show()
    }
}

fun Activity.getProgressiveDialog(
    title: String = this.getString(R.string.app_name),
    message: String = "Loading..",
    negativeText: String = this.getString(R.string.label_cancel),
    isCancellable: Boolean = true,
    listener: DialogInterface.OnClickListener? = null
): ProgressiveDialogModel {
    val inflateLayout = ViewUtils.getViewFromLayout(this, R.layout.d_m_progress)
    val progressBar = inflateLayout.findViewById<ProgressBar>(R.id.progressBar)

    val materialAlertDialogBuilder = MaterialAlertDialogBuilder(this)
    materialAlertDialogBuilder.setView(inflateLayout)
    materialAlertDialogBuilder.create().window?.setBackgroundDrawable(
        ColorDrawable(
            Color.TRANSPARENT
        )
    )
    materialAlertDialogBuilder.setTitle(title)
    materialAlertDialogBuilder.setCancelable(isCancellable)
    materialAlertDialogBuilder.setMessage(message)
    if (isCancellable) {
        listener?.let {
            materialAlertDialogBuilder.setNegativeButton(negativeText, listener)
        }
    }
    materialAlertDialogBuilder.show()
    return ProgressiveDialogModel(materialAlertDialogBuilder, inflateLayout, progressBar)
}

fun Activity.getLoaderDialog(
    title: String = this.getString(R.string.app_name),
    message: String = "",
    isCancellable: Boolean = false
): ProgressDiag {
    val progressDialog = ProgressDiag(this)
    progressDialog.setTitle(title)
    progressDialog.setMessage(message)
    progressDialog.setCancelable(isCancellable)
    progressDialog.show()

    return progressDialog
}

fun Activity.getInformationDialog(
    title: String = this.getString(R.string.app_name),
    message: String? = null,
    positiveText: String = this.getString(R.string.label_ok),
    isCancellable: Boolean = true,
    listener: DialogInterface.OnClickListener? = null
) {
    val materialAlertDialogBuilder = MaterialAlertDialogBuilder(this)
    materialAlertDialogBuilder.setTitle(title)
    materialAlertDialogBuilder.setCancelable(isCancellable)
    message?.let {
        materialAlertDialogBuilder.setMessage(it)
    }
    materialAlertDialogBuilder.setPositiveButton(positiveText) { dialog, which ->
        listener?.onClick(dialog, which)
        materialAlertDialogBuilder.create().dismiss()
    }.show()
}