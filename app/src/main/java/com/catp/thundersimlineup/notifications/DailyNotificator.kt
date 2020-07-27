package com.catp.thundersimlineup.notifications

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.await
import com.catp.model.JsonRules
import com.catp.thundersimlineup.LocalDateTimeProvider
import com.catp.thundersimlineup.data.Preferences
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

    @Inject
    lateinit var preferences: Preferences

    private val tag = "send_reminder_periodic"

    fun createNotificationTask(context: Context) {
        GlobalScope.launch {
            val workManager = WorkManager.getInstance(context)
            cancelCurrentTask(workManager)
            if (preferences.showDailyNotification)
                createNewTask(
                    workManager/*,
                dateTimeProvider.now().hour,
                dateTimeProvider.now().plusMinutes(1).minute*/
                )
        }
    }


    fun createNewTask(
        workManager: WorkManager,
        hour: Int = JsonRules.LINEUP_UTC_TIME_OF_CHANGE,
        minute: Int = 0
    ) {
        val initialDelay = initialDelay(hour, minute)

        val workRequest = OneTimeWorkRequestBuilder<NotificationWork>()
            .setInitialDelay(initialDelay.toMillis(), TimeUnit.MILLISECONDS)
            .addTag(tag)
            .build()

        workManager.enqueueUniqueWork(
            tag,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    private fun initialDelay(hour: Int, minute: Int): Duration {

        val now = dateTimeProvider.now()
        return if (!Duration.between(
                now, now.withHour(hour).withMinute(minute).withSecond(0)
            ).isNegative
        ) {
            Duration.between(
                now,
                now
                    .withHour(hour)
                    .withMinute(minute)
            )
        } else
            Duration.between(
                now,
                now
                    .plusDays(1)
                    .withHour(hour)
                    .withMinute(minute)
            )
    }

    private suspend fun cancelCurrentTask(workManager: WorkManager) {
        workManager.cancelAllWorkByTag(tag).await()
        workManager.pruneWork().await()
    }
}