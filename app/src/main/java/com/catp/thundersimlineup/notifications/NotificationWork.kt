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
import com.catp.thundersimlineup.log
import com.google.firebase.analytics.FirebaseAnalytics


class NotificationWork(
    appContext: Context,
    workerParams: WorkerParameters,
    val lineupDao: LineupDao,
    private val dailyNotificator: DailyNotificator,
    val schedule: Schedule,
    private val localDateTimeProvider: LocalDateTimeProvider
) :
    Worker(appContext, workerParams) {
    private val channelId = this.javaClass.name
    override fun doWork(): Result {
        log("🌈NW: doWork: $applicationContext")
        val favorites = getFavoritesForToday()
        log("🌈NW: fav: $favorites")
        logFavorites()
        log("🌈NW: logDone")
        showNotification(favorites)
        log("🌈NW: showNotify done")
        reschedule()
        log("🌈NW: reschedule done")
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
                val channel = NotificationChannel(channelId, name, importance).apply {
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
            val notification = NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Favorite vehicles ready to roll")
                .setContentText(favorites.joinToString("\n") { it.title })
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
        log("🐷getFavoritesForToday fav: $vehicles")
        val result = mutableListOf<Vehicle>()
        if (vehicles.isNotEmpty()) {
            schedule.updateRule()
            val lowLineups =
                schedule.getLineupForDate(localDateTimeProvider.now().toLocalDate(), LineupType.LOW)
            val topLineups =
                schedule.getLineupForDate(localDateTimeProvider.now().toLocalDate(), LineupType.TOP)
            log("🐷getFavoritesForToday lineups: ${lowLineups?.lineupEntity?.name} and ${topLineups?.lineupEntity?.name}")
            listOfNotNull(lowLineups, topLineups).forEach { lineup ->
                result += listOf(lineup.teamA.vehicles, lineup.teamB.vehicles)
                    .flatten()
                    .filter { it in vehicles }
            }
        }
        return result
    }

    private fun reschedule() {
        dailyNotificator.createNewTask(
            WorkManager.getInstance(applicationContext)
        )
    }

}