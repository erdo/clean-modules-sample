package foo.bar.example.clean.ui.dashboard

import android.animation.*
import android.animation.ValueAnimator.INFINITE
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import co.early.fore.core.ui.SyncableView
import foo.bar.example.clean.ui.common.anim.CustomEasing
import foo.bar.example.clean.ui.common.anim.allowAnimationOutsideParent

const val MAX_ROTATIONS_PER_SEC = 1.0
const val POLLEN_LEVEL_CHANGE_DURATION_MS = 1000L

/**
 * We handle the pollen level and windspeed change animations here.
 * The MaxMinIndicator and PercentPie widgets handle their
 * own animations
 */
class DashboardAnimations(
    private val windTurbine: View,
    private val pollenView: View,
    private val grassView: View,
    private val syncableView: SyncableView
) {

    private var windSpeedAnim = ObjectAnimator.ofFloat(windTurbine, "rotation", 0f, 360f).apply {
        repeatCount = INFINITE
        interpolator = CustomEasing.straightNoChaser
    }

    private val changePollenAnimSet = AnimatorSet().apply {
        interpolator = CustomEasing.upRamp
        duration = POLLEN_LEVEL_CHANGE_DURATION_MS
        addListener(
            object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    // Handler otherwise when we draw, the animation
                    // is still "running" on older android
                    Handler(Looper.getMainLooper()).postDelayed({
                        grassView.visibility = VISIBLE
                        syncableView.syncView()
                    }, 10)
                }
            }
        )
    }

    init {
        windTurbine.allowAnimationOutsideParent()
        pollenView.allowAnimationOutsideParent()
    }

    fun animateWindSpeedChange(percent: Float) {

        val boundedPercent = minOf(maxOf(percent, 0f), 100f)
        val rotationsPerSecond = (boundedPercent.toDouble() * MAX_ROTATIONS_PER_SEC) / 100.0
        val period = (1000.0 / rotationsPerSecond).toLong()

        windSpeedAnim = ObjectAnimator.ofFloat(
            windTurbine,
            "rotation",
            windSpeedAnim.animatedValue as Float,
            windSpeedAnim.animatedValue as Float + 360f
        ).apply {
            repeatCount = INFINITE
            interpolator = CustomEasing.straightNoChaser
            duration = period
        }

        windSpeedAnim.start()
    }

    fun animatePollenChange() {

        changePollenAnimSet.apply {
            playTogether(
                ObjectAnimator.ofFloat(pollenView, "alpha", 0f, 1f)
            )
        }

        // temporarily adjust things before running animation, syncView() gets
        // called at the end of the animation anyway to put everything back to how it should be
        grassView.visibility = INVISIBLE
        changePollenAnimSet.start()
    }
}
