package com.domain.models


data class CurrentUserSipModel(
    var displayName: String = "",
    var sipIdentity: String = "",
    var sipDomain: String = "",
    var password: String = "",
    var signalingTransportType: Int = 1,
    var keepAliveInterval: Int = 30,
    var verifyTLSServer: Boolean = false,
    var rtpPort: Int = 0,
    var sipPort: Int = 0,
    var registerTimeout: Int = 30 * 60,
    var hangupTimeout: Int = 10 * 60,
    var autoSendRtpVideo: Boolean = false,
    var autoSendRtpAudio: Boolean = false,
    var videoQuality: String = "VIDEO_MODE_DEFAULT"
)