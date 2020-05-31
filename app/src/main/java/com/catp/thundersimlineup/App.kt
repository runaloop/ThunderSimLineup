package com.catp.thundersimlineup

import android.app.Application
import com.catp.thundersimlineup.annotation.ApplicationScope
import com.catp.thundersimlineup.data.DataModule
import com.catp.thundersimlineup.data.db.DBModule
import com.facebook.stetho.Stetho
import com.jakewharton.threetenabp.AndroidThreeTen
import toothpick.Scope
import toothpick.ktp.KTP
import toothpick.ktp.binding.bind
import toothpick.ktp.binding.module


class App : Application() {
    lateinit var scope: Scope


    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        scope.release()
    }

    override fun onCreate() {
        super.onCreate()
        scope = KTP.openScope(ApplicationScope::class.java)
            .installModules(module {
                bind<Application>().toInstance { this@App }
            }, DBModule(this), DataModule())
        AndroidThreeTen.init(this)
        Stetho.initializeWithDefaults(this)
    }


}