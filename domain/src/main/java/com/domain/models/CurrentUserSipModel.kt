package com.domain.models



data class CurrentUserSipModel(
        var displayName: String = "",
        var sipIdentity: String = "",
        var sipDomain: String = "",
        var password: String = "",
        var signalingTransportType: Int = 2,
        var keepAliveInterval: Int = 2
)