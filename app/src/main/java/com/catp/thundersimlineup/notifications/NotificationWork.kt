package com.catp.thundersimlineup.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.catp.thundersimlineup.LocalDateTimeProvider
import com.catp.thundersimlineup.MainActivity
import com.catp.thundersimlineup.R
import com.catp.thundersimlineup.data.Schedule
import com.catp.thundersimlineup.data.db.LineupDao
import com.catp.thundersimlineup.data.db.entity.LineupType
import com.catp.thundersimlineup.data.db.entity.Vehicle
import com.google.firebase.analytics.FirebaseAnalytics


class NotificationWork(
    appContext: Context,
    workerParams: WorkerParameters,
    val lineupDao: LineupDao,
    val dailyNotificator: DailyNotificator,
    val schedule: Schedule,
    val localDateTimeProvider: LocalDateTimeProvider
) :
    Worker(appContext, workerParams) {
    val CHANNEL_ID = this.javaClass.name
    override fun doWork(): Result {
        val favorites = getFavoritesForToday()
        logFavorites()
        showNotification(favorites)
        reschedule()
        return Result.success()
    }

    private fun logFavorites() {
        val params = Bundle()
        params.putInt("count", lineupDao.getFavoriteVehicles().size)
        FirebaseAnalytics.getInstance(applicationContext).logEvent("DailyFavorite", params)
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            with(applicationContext) {
                val name = getString(R.string.channel_name)
                val descriptionText = getString(R.string.channel_description)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
                // Register the channel with the system
                val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    private fun showNotification(favorites: List<Vehicle>) {
        if (favorites.isNotEmpty()) {
            createNotificationChannel()
            val intent = Intent(applicationContext, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent =
                PendingIntent.getActivity(applicationContext, 0, intent, 0)
            val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Favorite vehicles ready to roll")
                .setContentText(favorites.map { it.title }.joinToString("\n"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            val service =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            service.notify(1, notification)
        }
    }

    private fun getFavoritesForToday(): List<Vehicle> {
        val vehicles = lineupDao.getFavoriteVehicles()
        val result = mutableListOf<Vehicle>()
        if (vehicles.isNotEmpty()) {
            schedule.updateRule()
            val lowLineups =
                schedule.getLineupForDate(localDateTimeProvider.now().toLocalDate(), LineupType.LOW)
            val topLineups =
                schedule.getLineupForDate(localDateTimeProvider.now().toLocalDate(), LineupType.TOP)
            listOfNotNull(lowLineups, topLineups).forEach { lineup ->
                result += listOf(lineup.teamA.vehicles, lineup.teamB.vehicles)
                    .flatten()
                    .filter { it in vehicles }
            }
        }
        return result
    }

    private fun reschedule() {
        val now = localDateTimeProvider.now()
        dailyNotificator.createNewTask(
            WorkManager.getInstance(applicationContext)
        )
    }

}