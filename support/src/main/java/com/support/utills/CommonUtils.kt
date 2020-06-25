package com.support.utills

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider

import com.google.gson.Gson
import com.support.R
import java.io.File


fun getProgress(progressed: Long, totalCount: Long) {
    ((progressed * 100f) / totalCount)
}

fun Activity.startCall(phoneNumber: String) {
    val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
    startActivity(intent)
}

fun Any.toJson(): String? {
    try {
        return Gson().toJson(this)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

fun Context.shareFile(file: File, emailAddress: String? = null) {
    val intentShareFile = Intent(Intent.ACTION_SEND)
    intentShareFile.type = "application/text" //FIXME : CORRECT THE MIME
    val uri = FileProvider.getUriForFile(
            this,
            "${this.packageName}.provider",
            file)
    intentShareFile.putExtra(Intent.EXTRA_STREAM, uri);
    emailAddress?.let {
        intentShareFile.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailAddress));
    }

    intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "${this.getString(R.string.app_name)} logDump")
    intentShareFile.putExtra(Intent.EXTRA_TEXT, "Device Name ${android.os.Build.MANUFACTURER + android.os.Build.MODEL}");


    //if you need
    //intentShareFile.putExtra(Intent.EXTRA_SUBJECT,"Sharing File Subject);
    //intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File Description");
    startActivity(Intent.createChooser(intentShareFile, "Share File"))
}