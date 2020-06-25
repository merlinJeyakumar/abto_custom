package com.support.baseApp.mvvm


import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.support.R
import com.support.baseApp.mvvm.permission.MEasyPermissions
import com.support.network.ConnectivityLiveData
import com.support.supportBaseClass.CustomProgressDialog
import com.support.widgets.dialog.MConfirmationDialog
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.ma_base_layout.*


abstract class MBaseActivity<B : ViewDataBinding, VM : MBaseViewModel> : AppCompatActivity(),
    IMBaseView {

    private val TAG = this::class.java.simpleName

    private lateinit var customProgressDialog: CustomProgressDialog
    private val compositeDisposable = CompositeDisposable()

    protected lateinit var binding: B
    protected lateinit var viewModel: VM
    protected abstract fun initializeViewModel(): VM

    companion object {
        const val REQ_CONTACT_PERMISSION = 121
        const val REQ_STORAGE_CAMERA_PERMISSION = 122
        const val REQ_STORAGE_PERMISSION = 123
        const val REQ_PHONE_PERMISSION = 126
        const val REQ_LOCATIOIN_PERMISSION = 124
        const val REQ_STORAGE_AUDIO_PERMISSION = 125
    }

    fun clearBackStack() {
        val backStackEntryCount = supportFragmentManager.backStackEntryCount
        if (backStackEntryCount > 0) {
            val firstEntry = supportFragmentManager.getBackStackEntryAt(0)
            clearBackStackInclusive(firstEntry.name.toString())
        }
    }

    fun addRxCall(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    private fun clearAllCalls() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }

    protected abstract fun getBaseLayoutId(): Int

    protected abstract fun setUpChildUI(savedInstanceState: Bundle?)

    protected open fun getProgressView(): View {
        return v_content_load_progress
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (activity is MActionBarActivity<*, *>) {
            setContentView(getBaseLayoutId())
        } else {
            setContentView(R.layout.ma_base_layout)
            a_base_layout_content!!.layoutResource = getBaseLayoutId()
            /*a_base_layout_content!!.inflate()*/
            binding = DataBindingUtil.bind(a_base_layout_content!!.inflate())!!
            binding.lifecycleOwner = this@MBaseActivity
            viewModel = initializeViewModel()
            setUpObserver()
        }

        ConnectivityLiveData(application)
            .observe(this, Observer { isConnected ->
                onNetworkConnectionChanged(isConnected)
            })

        setUpChildUI(savedInstanceState)
    }

    private fun setStatusBarGradient() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            val window = activity.window
            val grad_bg = ContextCompat.getDrawable(this, R.drawable.app_gradient_bgr)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
//             window.navigationBarColor = ContextCompat.getColor(this,android.R.color.transparent)
//             window.setBackgroundDrawable(grad_bg)

        }
    }

    private fun setUpObserver() {
        /*viewModel.showProgress.observe(this, Observer { show ->
            if (show) {
                showDataProgress()
            } else {
                hideDataProgress()
            }
        })*/

        viewModel.toastMessage.observe(this, Observer { message ->
            showToast(message)
        })

        viewModel.toastResMessage.observe(this, Observer { intRes ->
            showToast(intRes)
        })

        viewModel.showProgressDialog.observe(this, Observer { show ->
            if (show) {
                showCustomProgressDialog()
            } else {
                hideCustomProgressDialog()
            }
        })
    }

    public fun showDataProgress() {
        getProgressView().setBackgroundColor(ContextCompat.getColor(this, R.color.whiteLight25))
        getProgressView().visibility = View.VISIBLE
    }

    public fun hideDataProgress() {
        getProgressView().setBackgroundColor(
            ContextCompat.getColor(
                this,
                android.R.color.transparent
            )
        )
        getProgressView().visibility = View.GONE
    }

    public override fun showProgress() {
        getProgressView().visibility = View.VISIBLE
    }

    public override fun hideProgress() {
        getProgressView().visibility = View.GONE
    }

    override fun showToastMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun hideSoftKeyboard() {
        val view = currentFocus
        if (view != null) {
            val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    override fun showSoftKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager;
        inputMethodManager.toggleSoftInputFromWindow(
            view.getApplicationWindowToken(),
            InputMethodManager.SHOW_FORCED, 0
        );
    }

    fun replaceFragment(fragment: Fragment) {
        val tag = fragment.javaClass.name
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace((activity as IMFragmentSupport).getContainerLayoutId(), fragment, tag)
            .addToBackStack(tag).commit()
    }

    fun addFragment(fragment: Fragment, enableAnimation: Boolean = true) {

        val tag = fragment.javaClass.name
        val fragmentManager = supportFragmentManager
        var transaction = fragmentManager.beginTransaction()
        val currentFragment = getCurrentFragment()

        if (currentFragment == null || !currentFragment.javaClass.name.equals(tag)) {
            if (currentFragment != null) {

                if (enableAnimation) {
                    transaction = transaction
                        .setCustomAnimations(
                            R.anim.enter_fragment,
                            R.anim.exit_fragment,
                            R.anim.pop_enter_fragment,
                            R.anim.pop_exit_fragment
                        )
                }
            }
            transaction = transaction.add(
                (activity as IMFragmentSupport).getContainerLayoutId(),
                fragment,
                tag
            )/*.show(fragment)*/
            if (currentFragment != null) {
                transaction = transaction.hide(currentFragment)
            }
            //.show(fragment)
            /*if (currentFragment != null) {
                transaction = transaction.hide(currentFragment)
            }*/
            transaction.addToBackStack(tag)
                .commit()
        }
    }

    fun clearBackStackUpto(removeFragmentUptoFragmentId: String) {
        supportFragmentManager.popBackStackImmediate(removeFragmentUptoFragmentId, 0)
    }

    fun clearBackStackInclusive(removeFragmentUptoFragmentId: String) {
        supportFragmentManager.popBackStackImmediate(
            removeFragmentUptoFragmentId,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }

    fun isAvailableInBackStack(name: String): Boolean {
        val fm = supportFragmentManager
        for (entry in 0 until fm.backStackEntryCount) {
            val backStackEntryAt = fm.getBackStackEntryAt(entry)
            if (backStackEntryAt.name.equals(name)) {
                return true
            }
        }
        return false
    }

    private fun getCurrentFragment(): Fragment? {
        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.a_m_main_fl_fragment_container)
        return currentFragment
    }

    fun showConfirmationDialog(
        @StringRes title: Int,
        @StringRes infoMessage: Int
    ): MConfirmationDialog {
        val alertBuilder = MConfirmationDialog.Builder(this)
        alertBuilder.setTitle(resources.getString(title))
        alertBuilder.setMessage(getString(infoMessage))
        return alertBuilder.create()
    }

    fun showConfirmationDialog(
        @StringRes title: Int,
        @StringRes infoMessage: Int,
        positiveClickListener: DialogInterface.OnClickListener,
        negativeClickListener: DialogInterface.OnClickListener
    ): MConfirmationDialog {
        val alertBuilder = MConfirmationDialog.Builder(this)
        alertBuilder.setTitle(resources.getString(title))
        alertBuilder.setMessage(getString(infoMessage))
        alertBuilder.setOnPositiveClickListener(positiveClickListener)
        alertBuilder.setOnNegativeClickListener(negativeClickListener)
        return alertBuilder.create()
    }

    override fun onBackPressed() {
        if (activity is IMFragmentSupport) {
            if (supportFragmentManager.backStackEntryCount > 1) {
                val currentFragment = getCurrentFragment()
                if (currentFragment is IMBackSupport) {
                    currentFragment.onBackPress()
                } else {
                    supportFragmentManager.popBackStack()
                }
            } else {
                val currentFragment = getCurrentFragment()
                if (currentFragment is IMBackSupport) {
                    currentFragment.onBackPress()
                } else {
                    super.onBackPressed()
                }
            }
        } else {
            super.onBackPressed()
        }
    }

    val context: Context = this

    val activity: Activity = this

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
        val snackbar =
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val textView =
            snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.setTextColor(if (isSuccess) Color.GREEN else Color.WHITE)
        textView.maxLines = 3
        snackbar.show()
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
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun showToast(@StringRes resId: Int) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
    }

    /**
     * CHECK INTERNET CONNECTIVITY.
     */

    fun isInternetAvailable(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm?.activeNetworkInfo
        return netInfo != null && netInfo.isConnected && netInfo.isAvailable
    }

    fun showNoInternetAvailable() {
        val snackbar = Snackbar.make(
            findViewById(android.R.id.content),
            getString(R.string.msg_no_internet_available),
            Snackbar.LENGTH_LONG
        )
        val textView =
            snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.setTextColor(Color.RED)
        snackbar.show()
    }

    fun showCustomProgressDialog() {
        showCustomProgressDialog(false)
    }

    fun showCustomProgressDialog(canCancel: Boolean) {
        customProgressDialog = CustomProgressDialog(this)
        customProgressDialog.show()
        customProgressDialog.setCancelable(canCancel)
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

    /*fun obtainViewModel(): VM = obtainViewModel(viewModel.javaClass)*/

    fun showExplanationDialog(
        @StringRes infoMessage: Int,
        onClickListener: DialogInterface.OnClickListener
    ) {
        val alertBuilder = MConfirmationDialog.Builder(this)
        alertBuilder.setTitle("Permission necessary")
        alertBuilder.setMessage(getString(infoMessage))
        alertBuilder.setPositiveButton(R.string.label_continue)
            .setOnPositiveClickListener(onClickListener)
        val alert = alertBuilder.create()
        alert.setCancelable(false)
        alert.show()
    }

    fun onNetworkConnectionChanged(isConnected: Boolean) {
        onNetworkStatusChanged(isConnected)
        //publishSubject.onNext(isConnected)
        if (isConnected) {
        } else {
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    open fun onNetworkStatusChanged(isConnected: Boolean) {}

    override fun onDestroy() {
        clearAllCalls()
        super.onDestroy()
    }
}
