package com.merlin.abto.abto.utility

fun getSipProps(text: String): MutableList<String> {
    val sipIdDomain = text.split(";")[0]
    val sipPassword = text.split(";")[1]

    val sipId = sipIdDomain.split("@")[0]
    val sipDomain = sipIdDomain.split("@")[1]

    return mutableListOf<String>().apply {
        this.add(sipId)
        this.add(sipDomain)
        this.add(sipPassword)
    }
}
