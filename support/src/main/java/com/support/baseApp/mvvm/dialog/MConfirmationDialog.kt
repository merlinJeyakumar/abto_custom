package com.support.widgets.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.text.TextUtils
import android.view.View
import androidx.annotation.StringRes
import com.support.R
import kotlinx.android.synthetic.main.d_m_confirmation.*
import java.io.Serializable


class MConfirmationDialog(
    context: Context?,
    private val builder: Builder
) : MHeaderDialog(context) {

    override fun getHeaderTitle(): String {
        return if (builder.getTitle().isEmpty()) context.getString(R.string.app_name) else builder.getTitle()
    }

    override fun getLayoutId(): Int {
        return R.layout.d_m_confirmation
    }

    override fun setUpChildUi() {
        if (!TextUtils.isEmpty(builder.getMessage())) {
            d_confirmation_message.text = builder.getMessage()
        }
        if (builder.negativeTextResId != -1) {
            d_confirmation_cancel.setText(builder.negativeTextResId)
        }
        if (builder.positiveTextResId != -1) {
            d_confirmation_ok.setText(builder.positiveTextResId)
        }

        d_confirmation_ok.setOnClickListener {
            if (builder.getPositiveClickListener() == null) {
                dismiss()
            } else {
                builder.getPositiveClickListener()!!.onClick(this, Dialog.BUTTON_NEGATIVE)
            }
        }

        d_confirmation_cancel.setOnClickListener {
            if (builder.getNegativeClickListener() == null) {
                dismiss()
            } else {
                builder.getNegativeClickListener()!!.onClick(this, Dialog.BUTTON_NEGATIVE)
            }

        }

        d_confirmation_cancel.visibility = if (builder.showNegative) View.VISIBLE else View.GONE
    }

    fun getBuilder(): Builder {
        return builder
    }

    class Builder(private val context: Context) : Serializable {

        @StringRes
        var positiveTextResId = -1
            private set
        @StringRes
        var negativeTextResId = -1
            private set
        private var message = ""
        private var title = ""

        private var onPositiveButtonClickListener: DialogInterface.OnClickListener? = null
        private var onNegativeButtonClickListener: DialogInterface.OnClickListener? = null
        var showNegative = true

        fun setMessage(message: String): Builder {
            this.message = message.trim { it <= ' ' }
            return this
        }

        fun setTitle(title: String): Builder {
            this.title = title.trim { it <= ' ' }
            return this
        }

        fun setPositiveButton(@StringRes textResId: Int): Builder {
            this.positiveTextResId = textResId
            return this
        }

        fun setNegativeButton(@StringRes textResId: Int): Builder {
            this.negativeTextResId = textResId
            return this
        }

        fun setOnNegativeClickListener(onClickListener: DialogInterface.OnClickListener): Builder {
            this.onNegativeButtonClickListener = onClickListener
            return this
        }


        fun setOnPositiveClickListener(onClickListener: DialogInterface.OnClickListener): Builder {
            this.onPositiveButtonClickListener = onClickListener
            return this
        }


        fun getMessage(): String {
            return message
        }

        fun getTitle(): String {
            return title
        }

        fun getPositiveClickListener(): DialogInterface.OnClickListener? {
            return onPositiveButtonClickListener
        }

        fun getNegativeClickListener(): DialogInterface.OnClickListener? {
            return onNegativeButtonClickListener
        }

        fun create(): MConfirmationDialog {
            return MConfirmationDialog(context, this)
        }
    }
}