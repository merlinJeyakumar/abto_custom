package com.domain.schedulers

import io.reactivex.Scheduler

/**
 * Allow providing different types of [Scheduler]s.
 */
interface IScheduler {

    fun computation(): Scheduler

    fun io(): Scheduler

    fun ui(): Scheduler
}
