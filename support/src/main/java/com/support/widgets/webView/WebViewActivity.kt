package com.support.widgets.webView

import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.support.R
import com.support.network.ConnectivityLiveData
import kotlinx.android.synthetic.main.web_view_activity.*
import org.jetbrains.anko.toast


class WebViewActivity : AppCompatActivity() {

    companion object {

        const val WebView_URL = "WebView_URL"
        const val WebView_PAGE_TITLE = "WebView_PageTitle"

    }

    private var isNetworkAvailable = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.web_view_activity)

        ConnectivityLiveData(this.application)
            .observe(this, Observer { isConnected ->
                isNetworkAvailable = isConnected
                if(!isConnected)
                    toast("Please Connect to Internet")

            })


        intent?.extras?.getString(WebView_URL)?.let { pageUrl ->
                wv_root.loadUrl(pageUrl)
        }

        intent?.extras?.getString(WebView_PAGE_TITLE)?.let { pageTitle ->
            supportActionBar?.title = pageTitle
        }


        val settings = wv_root.settings
        settings.setSupportZoom(false)
        settings.javaScriptEnabled = true


        wv_root.webViewClient = MyBrowserClient()

        wv_root.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                progress_bar.progress = newProgress
                if (newProgress == 100)
                    progress_bar.visibility = View.GONE
                else
                    progress_bar.visibility = View.VISIBLE

            }
        }



    }


    private inner class MyBrowserClient : WebViewClient() {
       override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }
    }


}