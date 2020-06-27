package com.merlin.abto.abto.rxjava

import org.abtollc.api.SipMessage

class AbtoRxEvents {
    data class MessageReceived(
        var message: String,
        var senderSipId: String,
        var currentSipId: String
    )

    class MessageStatusChanged(
        messageState: MessageState,
        errorCode: Int = 0,
        sipMessage: SipMessage? = null
    )

    class AbtoConnectionChanged(
        val abtoState: AbtoState,
        val errorCode: Long = 0,
        val message: String = "",
        val throwable: Throwable? = null
    )

    class CallConnectionChanged(
        val state: CallState,
        val callId: Long = 0,
        val remoteContact: String? = null,
        val errorCode: Long = 0,
        val message: String = "",
        val throwable: Throwable? = null
    )

    class OnRemoteAlert(
        val callId: Int,
        val alert: RemoteAlert
    )
}

enum class AbtoState {
    INITIALIZING,
    INITIALIZED,
    INITIALIZATION_FAILED,
    REGISTERING,
    REGISTERING_FAILED,
    REGISTERED,
    UNREGISTERED,
    DESTROYED
}

enum class CallState {
    CONNECTING,
    CONNECTED,
    DISCONNECTED,
    LOCAL_HOLD,
    REMOTE_HOLD,
    ACTIVE,
    ERROR
}

enum class RemoteAlert {
    TRYING,
    RINGING,
    SESSION_PROGRESS,
    UNKNOWN
}

enum class MessageState {
    SENDING,
    SENT,
    ERROR
}