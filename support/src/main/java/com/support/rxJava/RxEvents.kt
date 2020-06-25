package com.support.rxJava



/**
 * original in https://android.jlelse.eu/rxbus-kotlin-listen-where-ever-you-want-e6fc0760a4a8
 */

class RxEvents {
    data class EventChatState(val chatState: String)
    data class FullFilmentNavigatorBus(val type : String, val jabberID:String)
    data class PeerChatNavigatorBus(val jabberID:String)
}