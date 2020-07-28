package com.catp.thundersimlineup.data.db.operation

import com.catp.thundersimlineup.data.Preferences
import com.google.firebase.crashlytics.FirebaseCrashlytics
import toothpick.InjectConstructor
import javax.inject.Inject

@InjectConstructor
class ErrorReporter {
    @Inject
    lateinit var preferences: Preferences

    fun process(error: Exception, message: String) {
        if (preferences.sendCrashLogs) {
            FirebaseCrashlytics.getInstance().recordException(
                RuntimeException(
                    message,
                    error
                )
            )
        }
    }
}