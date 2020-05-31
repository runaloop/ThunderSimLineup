package com.catp.thundersimlineup.data

import android.content.Context
import com.catp.thundersimlineup.whenNonNull
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import toothpick.InjectConstructor


const val REFRESH_PREFERENCE = "refresh pref"
const val REFRESH_LAST_TIME = "refresh last time"
const val REFRESH_ETAG = "refresh etag"

@InjectConstructor
class RefreshIntervalChecker(val netLoader: NetLoader) {

    fun isRefreshNeeded(context: Context): Boolean {
        val lastTime =
            context.getSharedPreferences(REFRESH_PREFERENCE, Context.MODE_PRIVATE).getString(
                REFRESH_LAST_TIME, ""
            )
        if (lastTime.isEmpty())
            return checkETag(context)
        else {
            val lastUpdateTime = Instant.parse(lastTime)
            if (Duration.between(lastUpdateTime, Instant.now()) > REFRESH_INTERVAL)
                return checkETag(context)
            return false
        }
    }

    private fun checkETag(context: Context): Boolean {
        try {
            val etag = netLoader.getETag()
            etag.whenNonNull {
                val localETag = context
                    .getSharedPreferences(REFRESH_PREFERENCE, Context.MODE_PRIVATE)
                    .getString(
                        REFRESH_ETAG, ""
                    )!!

                return if (localETag != this) {
                    setETag(context, this)
                    true
                } else
                    false
            }
            return false
        } catch (t: Throwable) {
            return false
        }
    }

    fun setETag(context: Context, ETag: String) {
        context.getSharedPreferences(REFRESH_PREFERENCE, Context.MODE_PRIVATE)
            .edit()
            .putString(REFRESH_ETAG, ETag)
            .commit()
    }

    fun setRefreshed(context: Context) {
        val time = Instant.now().toString()
        context.getSharedPreferences(REFRESH_PREFERENCE, Context.MODE_PRIVATE)
            .edit()
            .putString(REFRESH_LAST_TIME, time)
            .commit()
    }

    companion object {
        val REFRESH_INTERVAL: Duration = Duration.ofDays(1)
    }
}