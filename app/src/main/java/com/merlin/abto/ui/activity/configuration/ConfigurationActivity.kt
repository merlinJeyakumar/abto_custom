package com.merlin.abto.ui.activity.configuration

import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.merlin.abto.R
import com.merlin.abto.databinding.LayoutConfigurationBinding
import com.merlin.abto.extension.obtainViewModel
import com.support.baseApp.mvvm.MActionBarActivity
import com.support.dialog.getInputDialog
import com.support.dialog.getListDialog
import io.reactivex.functions.Predicate
import org.abtollc.sdk.AbtoPhoneCfg
import org.jetbrains.anko.toast

class ConfigurationActivity :
    MActionBarActivity<LayoutConfigurationBinding, ConfigurationViewModel>() {

    override fun getLayoutId(): Int {
        return R.layout.layout_configuration
    }

    override fun setUpUI(savedInstanceState: Bundle?) {
        viewModel.subscribe()

        initView()
        initObserver()
    }

    private fun initView() {
        //edSipId.setAdapter(viewModel.initAdapterAutoComplete())
    }

    private fun initObserver() {
    }

    override fun getHeaderTitle(): String {
        return "Client Configuration"
    }

    override fun isSupportBackOption(): Boolean {
        return true
    }

    override fun initializeViewModel(): ConfigurationViewModel {
        return obtainViewModel(ConfigurationViewModel::class.java).apply {
            binding.viewmodel = this
        }
    }

    override fun onNetworkStatusChanged(isConnected: Boolean) {
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_configuration, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_reset_default -> TODO()
        }
        return super.onOptionsItemSelected(item)
    }

    fun onSignalingTransport(view: View) {
        addRxCall(getListDialog(
            title = "Signaling Transport", listString = listOf(
                AbtoPhoneCfg.SignalingTransportType.TCP.name,
                AbtoPhoneCfg.SignalingTransportType.TLS.name,
                AbtoPhoneCfg.SignalingTransportType.UDP.name
            )
        ).map {
            if (it.boolean) {
                when (it.selectedItem) {
                    AbtoPhoneCfg.SignalingTransportType.UDP.name -> viewModel.setSignalingTransport(
                        AbtoPhoneCfg.SignalingTransportType.getTypeByValue(1)
                    )
                    AbtoPhoneCfg.SignalingTransportType.TCP.name -> viewModel.setSignalingTransport(
                        AbtoPhoneCfg.SignalingTransportType.getTypeByValue(2)
                    )
                    AbtoPhoneCfg.SignalingTransportType.TLS.name -> viewModel.setSignalingTransport(
                        AbtoPhoneCfg.SignalingTransportType.getTypeByValue(3)
                    )
                }
            }
            return@map it.boolean
        }.subscribe({
            if (it) {
                toast("Abto service restart required")
            }
        }, {
            toast(it.localizedMessage)
            it.printStackTrace()
        })
        )
    }

    fun onKeepAliveSignalingTransportInterval(view: View) {
        addRxCall(getInputDialog(
            title = "Keep Alive Signaling Transport Interval",
            inputType = InputType.TYPE_CLASS_NUMBER
        ).map {
            if (it.isPositive) {
                if (it.input.isEmpty() || (it.input.toInt() <= 0)) {
                    error("input not valid")
                } else {
                    viewModel.setKeepAliveInterval(it.input.toInt())
                }
            }
        }
            .retry(Predicate {
                return@Predicate true
            })
            .subscribe({
            }, {
                toast(it.localizedMessage)
                it.printStackTrace()
            })
        )
    }

    fun onRtpPort(view: View) {
        addRxCall(getInputDialog(
            title = "RTP Port",
            inputType = InputType.TYPE_CLASS_NUMBER
        ).map {
            if (it.isPositive) {
                if (it.input.isEmpty() || (it.input.toInt() <= 0)) {
                    error("input not valid")
                } else {
                    viewModel.setRtpPort(it.input.toInt())
                }
            }
        }
            .retry(Predicate {
                return@Predicate true
            })
            .subscribe({
            }, {
                toast(it.localizedMessage)
                it.printStackTrace()
            })
        )
    }

    fun onSipPort(view: View) {
        addRxCall(getInputDialog(
            title = "SIP Port",
            inputType = InputType.TYPE_CLASS_NUMBER
        ).map {
            if (it.isPositive) {
                if (it.input.isEmpty() || (it.input.toInt() <= 0)) {
                    error("input not valid")
                } else {
                    viewModel.setSipPort(it.input.toInt())
                }
            }
        }
            .retry(Predicate {
                return@Predicate true
            })
            .subscribe({
            }, {
                toast(it.localizedMessage)
                it.printStackTrace()
            })
        )

    }

    fun onVideoQuality(view: View) {}

    fun onAutoRtpVideo(view: View) {
        addRxCall(getListDialog(
            title = "Auto Send RTP Video", listString = listOf(
                true.toString(),
                false.toString()
            )
        ).map {
            if (it.boolean) {
                viewModel.setAutoSendRtpVideo(it.selectedItem?.contentEquals(true.toString())!!)
            }
            return@map it.boolean
        }.subscribe({
            if (it) {
                toast("Abto service restart required")
            }
        }, {
            toast(it.localizedMessage)
            it.printStackTrace()
        })
        )
    }

    fun onAutoRtpAudio(view: View) {
        addRxCall(getListDialog(
            title = "Auto Send RTP Audio", listString = listOf(
                true.toString(),
                false.toString()
            )
        ).map {
            if (it.boolean) {
                viewModel.setAutoSendRtpAudio(it.selectedItem?.contentEquals(true.toString())!!)
            }
            return@map it.boolean
        }.subscribe({
            if (it) {
                toast("Abto service restart required")
            }
        }, {
            toast(it.localizedMessage)
            it.printStackTrace()
        })
        )
    }
}
