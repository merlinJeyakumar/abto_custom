package com.support.rxJava

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Provides different types of schedulers.
 */
object Scheduler { // Prevent direct instantiation.

    fun computation(): Scheduler {
        return Schedulers.computation()
    }

    fun io(): Scheduler {
        return Schedulers.io()
    }

    fun ui(): Scheduler {
        return AndroidSchedulers.mainThread()
    }

}