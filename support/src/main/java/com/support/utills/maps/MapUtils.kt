package com.support.utills.maps

import android.location.Address

object MapUtils {

    fun generateSmallAddress(
        address: String
    ): String {
        val s = address.split(",")
        return if (s.size >= 3) s[1] + "," + s[2] else if (s.size == 2) s[1] else s[0]
    }

    fun generateLongAddress(addressList : List<Address>):String {
        addressList?.let { if(it.isNotEmpty()) return it[0].getAddressLine(0)}
        return ""
    }
}