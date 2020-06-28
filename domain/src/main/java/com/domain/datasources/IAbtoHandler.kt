package com.domain.datasources

import android.view.SurfaceView
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File

interface IAbtoHandler {
    fun sendMessage(receiverId: String, message: String): Completable
    fun dialCall(receiverId: String,isVideoCall: Boolean): Single<Int>
    fun hangupCall(callId: Int): Completable
    fun holdCall(callId: Int): Completable
    fun answerCall(callId: Int,isVideoCall: Boolean): Completable
    fun setVideoView(
        activeCallId: Int,
        localVideoSurface: SurfaceView,
        remoteVideoSurface: SurfaceView
    )
    fun setSpeakerMode(callId: Int, enable: Boolean)
    fun setMicMute(callId: Int, enable: Boolean)
    fun setVideoMute(callId: Int, enable: Boolean)
    fun startRecording(callId: Int, audioFile: File):Completable
    fun stopRecording(callId: Int)
    fun restartAbto():Completable
    fun unregisterAbto():Completable
    fun isAbtoRegistered(): Boolean
    fun isAbtoAccountAdded(): Boolean
    fun isAbtoInitialized(): Boolean
    fun isActiveCall(callId: Int): Boolean
    fun destroyAbtoService(): Completable
}