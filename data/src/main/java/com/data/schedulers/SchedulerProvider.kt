package com.data.schedulers

import com.domain.schedulers.IScheduler
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Provides different types of schedulers.
 */
class SchedulerProvider // Prevent direct instantiation.
private constructor() : IScheduler {

    override fun computation(): Scheduler {
        return Schedulers.computation()
    }

    override fun io(): Scheduler {
        return Schedulers.io()
    }

    override fun ui(): Scheduler {
        return AndroidSchedulers.mainThread()
    }

    companion object {
        var instance: SchedulerProvider = SchedulerProvider()
    }
}
