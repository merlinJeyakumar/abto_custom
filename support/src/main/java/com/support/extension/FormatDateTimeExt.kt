package com.mithrai.supportmodule.extension

import java.util.*

fun Long.formatTimerMillis(): String? {
    val _Default = "00:00:00"
    val _Time: String
    var hours = 0
    var seconds = 0
    var minutes = 0
    try {
        seconds = (Math.round(this.toString().toDouble() / 1000) % 60).toInt()
        minutes = ((this / (1000 * 60) % 60).toInt())
        hours = ((this / (1000 * 60 * 60)).toInt())
    } catch (e: Exception) {
        e.printStackTrace()
    }
    if (seconds == 0) {
        return _Default
    }
    return when {
        hours >= 1 -> {
            _Time = String.format(
                    Locale.getDefault(), "%02d:%02d:%02d",
                    hours,
                    minutes,
                    seconds
            )
            _Time
        }
        minutes >= 1 -> {
            _Time = String.format(
                    Locale.getDefault(), "00:%02d:%02d",
                    minutes,
                    seconds
            )
            _Time
        }
        else -> {
            _Time = String.format(
                    Locale.getDefault(), "00:00:%02d",
                    seconds
            )
            _Time
        }
    }
}