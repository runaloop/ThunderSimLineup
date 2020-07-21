package com.catp.thundersimlineup

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.View
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import toothpick.InjectConstructor

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
    let { lShift(size - n % size) }

@InjectConstructor
class LocalDateTimeProvider {
    fun now(): LocalDateTime = LocalDateTime.now(ZoneId.of("Z"))
}

fun progressBarStatus(value: Boolean, progressBar: View) {
    val loadingOpacity = 0.5f
    if (value) {
        progressBar.animation?.cancel()
        val animator = ObjectAnimator.ofFloat(progressBar, View.ALPHA, 0f, loadingOpacity)
        animator.duration=200
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
                progressBar.visibility = View.VISIBLE
                println("☠️ showing2")
            }
        })
        animator.start()
        progressBar.setOnTouchListener { view, event ->
            true
        }
        progressBar.setOnClickListener { view->
            true
        }
    } else {
        progressBar.animation?.cancel()
        val animator = ObjectAnimator.ofFloat(progressBar, View.ALPHA, loadingOpacity, 0f)
        animator.duration=200
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
                progressBar.visibility = View.VISIBLE
                println("☠️ showing")
            }

            override fun onAnimationEnd(animation: Animator?) {
                progressBar.visibility = View.GONE
                println("☠️ hiding")
            }
        })
        animator.start()
    }
}
