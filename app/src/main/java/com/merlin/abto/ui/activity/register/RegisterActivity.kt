package com.merlin.abto.ui.activity.register

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import com.merlin.abto.R
import com.merlin.abto.databinding.LayoutRegisterBinding
import com.merlin.abto.extension.obtainViewModel
import com.merlin.abto.ui.activity.configuration.ConfigurationActivity
import com.merlin.abto.ui.activity.main.MainActivity
import com.support.baseApp.mvvm.MActionBarActivity
import kotlinx.android.synthetic.main.layout_register.*

class RegisterActivity : MActionBarActivity<LayoutRegisterBinding, RegisterViewModel>() {

    override fun getLayoutId(): Int {
        return R.layout.layout_register
    }

    override fun setUpUI(savedInstanceState: Bundle?) {
        viewModel.subscribe()

        initUi()
        initObserver()
    }

    private fun initObserver() {
        viewModel.registerLiveData.observe(this@RegisterActivity, Observer {
            startActivity(
                Intent(
                    this@RegisterActivity,
                    MainActivity::class.java
                )
            )
            finish()
        })
    }

    override fun getHeaderTitle(): String {
        return resources.getString(R.string.app_name)
    }

    override fun isSupportBackOption(): Boolean {
        return false
    }

    override fun initializeViewModel(): RegisterViewModel {
        return obtainViewModel(RegisterViewModel::class.java).apply {
            binding.viewmodel = this
        }
    }

    override fun onNetworkStatusChanged(isConnected: Boolean) {
    }

    private fun initUi() {
        listview.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            viewModel.getSipList()
        );
        listview.setOnItemClickListener { parent, view, position, id ->
            val listItem: String = listview.adapter.getItem(position) as String
            viewModel.setUI(listItem)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_register, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_configuration -> startActivity(
                Intent(
                    this,
                    ConfigurationActivity::class.java //FIXME: NOT_WORKING_AS_EXPECTED_WHILE
                )
            )
        }
        return super.onOptionsItemSelected(item)
    }
}
