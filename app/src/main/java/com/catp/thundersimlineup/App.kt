package com.catp.thundersimlineup

import android.util.Log.VERBOSE
import androidx.multidex.MultiDexApplication
import androidx.work.Configuration
import com.catp.thundersimlineup.annotation.ApplicationScope
import com.catp.thundersimlineup.data.DataModule
import com.catp.thundersimlineup.data.Preferences
import com.catp.thundersimlineup.data.db.DBModule
import com.catp.thundersimlineup.notifications.WorkerFactory
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.jakewharton.threetenabp.AndroidThreeTen
import toothpick.Scope
import toothpick.ktp.KTP
import toothpick.smoothie.module.SmoothieApplicationModule
import javax.inject.Inject


class App : MultiDexApplication(), Configuration.Provider {
    lateinit var scope: Scope


    @Inject
    lateinit var workerFactory: WorkerFactory

    @Inject
    lateinit var preferences: Preferences


    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        scope.release()
    }

    override fun onCreate() {
        super.onCreate()
        scope = KTP.openScope(ApplicationScope::class.java)
            .installModules(SmoothieApplicationModule(this), DBModule(this), DataModule())
        scope.inject(this)
        AndroidThreeTen.init(this)
        firebaseInit()
    }

    private fun firebaseInit() {
        FirebaseApp.initializeApp(this)
        val instance = FirebaseCrashlytics.getInstance()
        if (!preferences.sendCrashLogs) {
            instance.deleteUnsentReports()
            instance.setCrashlyticsCollectionEnabled(false)
        } else {
            instance.setCrashlyticsCollectionEnabled(true)
        }
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(VERBOSE)
            .build()
    }
}