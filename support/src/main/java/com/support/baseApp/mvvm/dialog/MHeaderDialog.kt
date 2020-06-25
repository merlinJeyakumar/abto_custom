package com.support.widgets.dialog

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog
import com.support.R
import kotlinx.android.synthetic.main.d_m_header.*

abstract class MHeaderDialog(context: Context?) : AppCompatDialog(context) {

    protected abstract fun getLayoutId(): Int
    protected abstract fun getHeaderTitle(): String
    protected abstract fun setUpChildUi()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.d_m_header)

        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        val stub = findViewById<ViewStub>(R.id.d_header_layout_content)
        stub?.layoutResource = getLayoutId()
        stub?.inflate()
        setUpUI()
        setUpChildUi()
    }

    private fun setUpUI() {
        d_header_title.text = getHeaderTitle()
    }

    //abstract fun enableAnimation():Boolean

    fun showToastMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

}