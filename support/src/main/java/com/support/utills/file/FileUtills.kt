package com.support.utills.file

import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.nio.charset.Charset

object FileUtills {

    fun loadJSONFromAsset(activity: AppCompatActivity, fileName: String): String? {
        var json: String? = null
        try {
            val `is` = activity .assets.open(fileName)
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            json = String(buffer, Charset.forName("UTF-8"))
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }

        return json
    }
}