package com.catp.thundersimlineup.notifications

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.catp.thundersimlineup.LocalDateTimeProvider
import com.catp.thundersimlineup.data.Schedule
import com.catp.thundersimlineup.data.db.LineupDao
import toothpick.InjectConstructor
import javax.inject.Inject

@InjectConstructor
class WorkerFactory : WorkerFactory() {
    @Inject
    lateinit var lineupDao: LineupDao

    @Inject
    lateinit var dailyNotificator: DailyNotificator

    @Inject
    lateinit var schedule: Schedule

    @Inject
    lateinit var dateTimeProvider: LocalDateTimeProvider

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return NotificationWork(
            appContext,
            workerParameters,
            lineupDao,
            dailyNotificator,
            schedule,
            dateTimeProvider
        )
    }

}