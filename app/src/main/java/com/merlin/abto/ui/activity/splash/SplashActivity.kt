package com.merlin.abto.ui.activity.splash

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.merlin.abto.R
import com.merlin.abto.databinding.LayoutSplashBinding
import com.merlin.abto.extension.obtainViewModel
import com.merlin.abto.ui.activity.main.MainActivity
import com.merlin.abto.ui.activity.register.RegisterActivity
import com.support.baseApp.mvvm.MBaseActivity

class SplashActivity : MBaseActivity<LayoutSplashBinding, SplashViewModel>() {

    override fun initializeViewModel(): SplashViewModel {
        return obtainViewModel(SplashViewModel::class.java)
    }

    override fun onNetworkStatusChanged(isConnected: Boolean) {
    }

    override fun getBaseLayoutId(): Int {
        return R.layout.layout_splash
    }

    override fun setUpChildUI(savedInstanceState: Bundle?) {
        viewModel.subscribe()

        initObserver()
    }

    private fun initObserver() {
        viewModel.isLoggedIn.observe(this@SplashActivity, Observer {
            if (it) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            } else {
                startActivity(Intent(this@SplashActivity, RegisterActivity::class.java))
            }
            finish()
        })
    }
}
