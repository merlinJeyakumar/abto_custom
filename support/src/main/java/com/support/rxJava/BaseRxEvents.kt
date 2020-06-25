package com.support.rxJava



/**
 * original in https://android.jlelse.eu/rxbus-kotlin-listen-where-ever-you-want-e6fc0760a4a8
 */

class BaseRxEvents {

    data class ChatNavigatorBus(val type : String, val jabberID:String)

}