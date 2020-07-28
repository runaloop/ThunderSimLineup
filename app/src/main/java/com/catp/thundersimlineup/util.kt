package com.catp.thundersimlineup

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.view.View
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import toothpick.InjectConstructor
import javax.inject.Inject
import javax.inject.Singleton

inline fun <T> T?.whenNull(block: T?.() -> Unit): T? {
    if (this == null) block()
    return this@whenNull
}

inline fun <T> T?.whenNonNull(block: T.() -> Unit): T? {
    this?.block()
    return this@whenNonNull
}

fun <T> List<T>.lShift(n: Int) =
    let { slice(n % size until size) + slice(0 until n % size) }

fun <T> List<T>.rShift(n: Int) =
    lShift(size - n % size)

@InjectConstructor
class LocalDateTimeProvider {
    fun now(): LocalDateTime = LocalDateTime.now(ZoneId.of("Z"))
}

@InjectConstructor
class LocalDateProvider {
    fun now(): LocalDate = LocalDate.now(ZoneId.of("Z"))
}

@SuppressLint("ClickableViewAccessibility")
fun progressBarStatus(value: Boolean, progressBar: View) {
    val loadingOpacity = 0.5f
    if (value) {
        progressBar.animation?.cancel()
        val animator = ObjectAnimator.ofFloat(progressBar, View.ALPHA, 0f, loadingOpacity)
        animator.duration = 200
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
                progressBar.visibility = View.VISIBLE
            }
        })
        animator.start()

        progressBar.setOnTouchListener { _, _ ->
            true
        }
        progressBar.setOnClickListener {
            true
        }
    } else {
        progressBar.animation?.cancel()
        val animator = ObjectAnimator.ofFloat(progressBar, View.ALPHA, loadingOpacity, 0f)
        animator.duration = 200
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
                progressBar.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animator?) {
                progressBar.visibility = View.GONE
            }
        })
        animator.start()
    }
}

@InjectConstructor
@Singleton
class StatUtil {
    @Inject
    lateinit var preferences: com.catp.thundersimlineup.data.Preferences

    fun sendViewStat(fragment: Fragment, title: String) {
        if (preferences.sendStat)
            FirebaseAnalytics.getInstance(fragment.requireContext())
                .setCurrentScreen(fragment.requireActivity(), title, null)
    }
}

@InjectConstructor
@Singleton
class CrashOnCondition {
    val hash = mutableMapOf<String, Int>()
    fun crashOnCount(count: Int, message: String) {
        println("🤛$message $count $hash")
        hash[message].whenNull {
            hash[message] = count
        }.whenNonNull {
            hash[message] = this - 1
        }
        if (hash[message]!! <= 0) {
            hash.remove(message)
            throw RuntimeException(message)
        }
    }

    fun crashOnlyOnce(message: String){
        hash[message].whenNull {
            hash[message] = 1
            throw RuntimeException(message)
        }
    }
}
