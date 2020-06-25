package com.support.utills.text

import android.text.TextUtils
import android.util.Patterns
import java.lang.StringBuilder
import java.util.regex.Pattern

object TextUtills {

    fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    fun createSeparatedStringFromList(list: List<String>) : String{

        val data = StringBuilder()

        for (datum in list) {
            data.append(datum)
            data.append(",")
        }

        data.substring(0,data.length-1)
        data.deleteCharAt(data.lastIndex)
        data.append(".")

        return data.toString()
    }

    fun getVideoIdFromYoutubeUrl(url: String): String {
        var videoId: String? = null
        val regex =
            "http(?:s)?:\\/\\/(?:m.)?(?:www\\.)?youtu(?:\\.be\\/|be\\.com\\/(?:watch\\?(?:feature=youtu.be\\&)?v=|v\\/|embed\\/|user\\/(?:[\\w#]+\\/)+))([^&#?\\n]+)"
        val pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(url)
        if (matcher.find()) {
            videoId = matcher.group(1)
        }
        return videoId?:"9xwazD5SyVg"
    }

    fun getRandomString(length: Int) : String {
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }
}