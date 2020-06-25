package com.support.utills

import android.content.pm.PackageManager
import java.io.UnsupportedEncodingException
import java.net.URL
import java.net.URLDecoder
import java.text.DecimalFormat
import java.util.*


object GeneralUtils {

    fun generateRandomID():String{
        val ts = System.currentTimeMillis().toString()
        val rand = getAlphaNumericString(8).toString()
        return ts + rand
    }

    fun getAlphaNumericString(n: Int): String? { // chose a Character random from this String
        val AlphaNumericString = ("ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz")
        // create StringBuffer size of AlphaNumericString
        val sb = StringBuilder(n)
        for (i in 0 until n) { // generate a random number between
// 0 to AlphaNumericString variable length
            val index = (AlphaNumericString.length
                    * Math.random()).toInt()
            // add Character one by one in end of sb
            sb.append(
                AlphaNumericString[index]
            )
        }
        return sb.toString()
    }

    fun isPackageInstalled(packageName: String, packageManager: PackageManager): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }

    }


    @Throws(UnsupportedEncodingException::class)
    fun splitQuery(url: URL): Map<String, List<String>> {
        val query_pairs = LinkedHashMap<String, MutableList<String>>()
        val pairs = url.query.split("&")
        for (pair in pairs) {
            val idx = pair.indexOf("=")
            val key = if (idx > 0) URLDecoder.decode(pair.substring(0, idx), "UTF-8") else pair
            if (!query_pairs.containsKey(key)) {
                query_pairs[key] = LinkedList()
            }
            val value = if (idx > 0 && pair.length > idx + 1) URLDecoder.decode(
                pair.substring(idx + 1),
                "UTF-8"
            ) else null
            query_pairs[key]?.add(value!!)
        }
        return query_pairs
    }

    fun splitQueryParams(queryParams : String): Map<String, List<String>> {
        val query_pairs = LinkedHashMap<String, MutableList<String>>()
        val pairs = queryParams .split("&")
        for (pair in pairs) {
            val idx = pair.indexOf("=")
            val key = if (idx > 0) URLDecoder.decode(pair.substring(0, idx), "UTF-8") else pair
            if (!query_pairs.containsKey(key)) {
                query_pairs[key] = LinkedList()
            }
            val value = if (idx > 0 && pair.length > idx + 1) URLDecoder.decode(
                pair.substring(idx + 1),
                "UTF-8"
            ) else null
            query_pairs[key]?.add(value?:"")
        }
        return query_pairs
    }

     val df2 = DecimalFormat("#.##")


}