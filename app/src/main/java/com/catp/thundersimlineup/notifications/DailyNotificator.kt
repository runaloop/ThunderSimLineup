package com.catp.thundersimlineup.notifications

import android.content.Context
import androidx.work.*
import com.catp.model.JsonRules
import com.catp.thundersimlineup.LocalDateTimeProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.threeten.bp.Duration
import toothpick.InjectConstructor
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@InjectConstructor
class DailyNotificator {
    @Inject
    lateinit var dateTimeProvider: LocalDateTimeProvider


    private val TASK_TAG = "send_reminder_periodic"

    fun createNotificationTask(context: Context) {
        GlobalScope.launch {
            val workManager = WorkManager.getInstance(context)
            cancelCurrentTask(workManager)
            createNewTask(
                workManager,
                dateTimeProvider.now().hour,
                dateTimeProvider.now().plusMinutes(1).minute
            )
        }
    }


    fun createNewTask(
        workManager: WorkManager,
        hour: Int = JsonRules.LINEUP_UTC_TIME_OF_CHANGE,
        minute: Int = 0
    ) {
        val initialDelay = initialDelay(hour, minute)

        println("🌈${initialDelay}")
        val workRequest = OneTimeWorkRequestBuilder<NotificationWork>()
            .setInitialDelay(initialDelay.toMillis(), TimeUnit.MILLISECONDS)
            .addTag(TASK_TAG)
            .build()

        workManager.enqueueUniqueWork(
            TASK_TAG,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    private fun initialDelay(hour: Int, minute: Int): Duration {
        return if (dateTimeProvider.now().hour <= hour) {
            Duration.between(
                dateTimeProvider.now(),
                dateTimeProvider.now()
                    .withHour(hour)
                    .withMinute(minute)
            )
        } else
            Duration.between(
                dateTimeProvider.now(),
                dateTimeProvider.now()
                    .plusDays(1)
                    .withHour(hour)
                    .withMinute(minute)
            )
    }

    private suspend fun cancelCurrentTask(workManager: WorkManager) {
        workManager.cancelAllWorkByTag(TASK_TAG).await()
        workManager.pruneWork().await()
    }
}