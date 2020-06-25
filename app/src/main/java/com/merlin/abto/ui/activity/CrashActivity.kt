package com.merlin.abto.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.merlin.abto.R
import kotlinx.android.synthetic.main.activity_crash.*

class CrashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crash)

        initView()
    }

    private fun initView() {
        //textView.text = intent?.getStringExtra(CRASH_ISSUE)
    }

    companion object {
        @JvmField
        var CRASH_ISSUE: String = "CRASH_ISSUE"
    }
}
