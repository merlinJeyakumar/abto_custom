package com.support.baseApp.mvvm


import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.support.R
import com.support.baseApp.mvvm.permission.MEasyPermissions
import com.support.supportBaseClass.CustomProgressDialog
import com.support.widgets.dialog.MConfirmationDialog
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.jetbrains.anko.support.v4.toast


abstract class MBaseFragment<B : ViewDataBinding, VM : MBaseViewModel> : Fragment(), IMBaseView {

    private lateinit var customProgressDialog: CustomProgressDialog
    private val compositeDisposable = CompositeDisposable()


    protected lateinit var binding: B
    protected lateinit var viewModel: VM
    protected abstract fun initializeViewModel(): VM
    protected abstract fun getLayoutResID(): Int
    protected lateinit var inflateView: View

    companion object {
        const val REQ_CONTACT_PERMISSION = 121
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        inflateView = inflater.inflate(getLayoutResID(), container, false)
        binding = DataBindingUtil.bind(inflateView)!!

        readIntent()

        initializeViewModel()

        return inflateView
    }

    fun addRxCall(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    private fun clearAllCalls() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }

    private fun setUpObserver() {
        viewModel.showProgressDialog.observe(this, Observer { show ->
            if (show) {
                showCustomProgressDialog()
            } else {
                hideCustomProgressDialog()
            }
        })
    }

    public fun showDataProgress() {
        if (activity is MBaseActivity<*, *>) {
            (activity as MBaseActivity<*, *>).showDataProgress()
        }
    }

    public fun hideDataProgress() {
        if (activity is MBaseActivity<*, *>) {
            (activity as MBaseActivity<*, *>).hideDataProgress()
        }
    }

    public override fun showProgress() {
        if (activity is MBaseActivity<*, *>) {
            (activity as MBaseActivity<*, *>).showProgress()
        }
    }

    public override fun hideProgress() {
        if (activity is MBaseActivity<*, *>) {
            (activity as MBaseActivity<*, *>).hideProgress()
        }
    }

    override fun showToastMessage(message: String) {

    }

    override fun hideSoftKeyboard() {
        if (activity is MBaseActivity<*, *>) {
            (activity as MBaseActivity<*, *>).hideSoftKeyboard()
        }
    }

    override fun showSoftKeyboard(view: View) {
        if (activity is MBaseActivity<*, *>) {
            (activity as MBaseActivity<*, *>).showSoftKeyboard(view)
        }
    }

    fun showMessage(@StringRes resId: Int) {
        showMessage(true, getString(resId))
    }

    fun showMessage(message: String) {
        showMessage(true, message)
    }

    fun showMessage(isSuccess: Boolean, @StringRes resId: Int) {
        showMessage(isSuccess, getString(resId))
    }

    fun showMessage(isSuccess: Boolean, message: String) {
        if (activity is MBaseActivity<*, *>) {
            (activity as MBaseActivity<*, *>).showMessage(isSuccess, message)
        }
    }

    fun showErrorMessage() {
        showMessage(false, R.string.msg_please_try_again)
    }

    fun showErrorMessage(body: String) {
        showMessage(
            false,
            if (TextUtils.isEmpty(body)) getString(R.string.msg_please_try_again) else body
        )
    }

    fun showErrorMessage(@StringRes strResId: Int) {
        val message = getString(strResId)
        showMessage(
            false,
            if (TextUtils.isEmpty(message)) getString(R.string.msg_please_try_again) else message
        )
    }

    fun showToast(msg: String) {
        toast(msg)
    }

    fun showToast(@StringRes resId: Int) {
        toast(resId)
    }

    fun isInternetAvailable(): Boolean {
        return if (activity is MBaseActivity<*, *>) {
            (activity as MBaseActivity<*, *>).isInternetAvailable()
        } else {
            (activity as MBaseActivity<*, *>).isInternetAvailable()
        }
    }

    fun showNoInternetAvailable() {

        if (activity is MBaseActivity<*, *>) {
            (activity as MBaseActivity<*, *>).showNoInternetAvailable()
        }

    }

    fun showCustomProgressDialog() {
        showCustomProgressDialog(false)
    }

    fun showCustomProgressDialog(canCancel: Boolean) {
        if (activity is MBaseActivity<*, *>) {
            (activity as MBaseActivity<*, *>).showCustomProgressDialog(canCancel)
        }
    }

    fun hideCustomProgressDialog() {
        if (::customProgressDialog.isInitialized && customProgressDialog.isShowing()) {
            customProgressDialog.dismiss()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        MEasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun showExplainationDialog(
        @StringRes infoMessage: Int,
        onClickListener: DialogInterface.OnClickListener
    ) {
        if (activity is MBaseActivity<*, *>) {
            (activity as MBaseActivity<*, *>).showExplanationDialog(infoMessage, onClickListener)
        }
    }

    fun showConfirmationDialog(
        @StringRes title: Int,
        @StringRes infoMessage: Int,
        positiveClickListener:DialogInterface.OnClickListener,
        negativeClickListener:DialogInterface.OnClickListener
    ): MConfirmationDialog {
        val alertBuilder = MConfirmationDialog.Builder(getBaseActivity())
        alertBuilder.setTitle(resources.getString(title))
        alertBuilder.setMessage(getString(infoMessage))
        alertBuilder.setOnPositiveClickListener(positiveClickListener)
        alertBuilder.setOnNegativeClickListener(negativeClickListener)
        return alertBuilder.create()
    }

    open fun readIntent() {}

    fun getBaseActivity(): MBaseActivity<*, *> {
        return activity as MBaseActivity<*, *>
    }

    override fun onDestroy() {
        clearAllCalls()
        super.onDestroy()
    }
}
