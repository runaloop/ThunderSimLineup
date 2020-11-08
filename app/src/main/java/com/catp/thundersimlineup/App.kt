package com.catp.thundersimlineup

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
import toothpick.Toothpick
import toothpick.ktp.KTP
import toothpick.smoothie.module.SmoothieApplicationModule
import javax.inject.Inject


class App : MultiDexApplication(), Configuration.Provider {
    lateinit var scope: Scope


    @Inject
    lateinit var workerFactory: WorkerFactory

    @Inject
    lateinit var preferences: Preferences

    override fun onCreate() {
        super.onCreate()
        tpInit()
        AndroidThreeTen.init(this)
        firebaseInit()
    }

    private fun tpInit() {
        scope = KTP.openScope(ApplicationScope::class.java)
            .installModules(SmoothieApplicationModule(this), DBModule(this), DataModule())
        scope.inject(this)
        if (BuildConfig.DEBUG)
            Toothpick.setConfiguration(toothpick.configuration.Configuration.forDevelopment())
    }

    private fun firebaseInit() {
        FirebaseApp.initializeApp(this)
        val instance = FirebaseCrashlytics.getInstance()
        if (!preferences.sendCrashLogs || BuildConfig.DEBUG) {
            instance.deleteUnsentReports()
            instance.setCrashlyticsCollectionEnabled(false)
        } else {
            instance.setCrashlyticsCollectionEnabled(true)
        }
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            //.setMinimumLoggingLevel(VERBOSE)
            .build()
    }
}