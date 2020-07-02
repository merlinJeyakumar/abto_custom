package com.domain.models


data class CurrentUserSipModel(
    var displayName: String = "",
    var sipIdentity: String = "",
    var sipDomain: String = "",
    var password: String = "",
    var signalingTransportType: Int = 1,
    var verifyTLSServer: Boolean = false,
    var rtpPort: Int = 0,
    var sipPort: Int = 0,
    var keepAliveInterval: Int = 30,
    var registerTimeout: Int = 30 * 1000,
    var hangupTimeout: Int = 10 * 1000,
    var inviteTimeout: Int = 3000,
    var autoSendRtpVideo: Boolean = true,
    var autoSendRtpAudio: Boolean = true,
    var videoQuality: String = "VIDEO_MODE_DEFAULT"
)