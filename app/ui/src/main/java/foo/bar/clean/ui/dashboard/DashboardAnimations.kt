package foo.bar.clean.ui.dashboard

import android.animation.*
import android.animation.ValueAnimator.INFINITE
import android.view.View
import foo.bar.clean.ui.common.anim.CustomEasing
import foo.bar.clean.ui.common.anim.allowAnimationOutsideParent

const val MAX_ROTATIONS_PER_SEC = 1.0
const val POLLEN_LEVEL_CHANGE_DURATION_MS = 200L
const val GRASS_ZOOM_DURATION_MS = 500L

/**
 * We handle the pollen level and windspeed change animations here.
 * The MaxMinIndicator and PercentPie widgets handle their
 * own animations
 */
class DashboardAnimations(
    private val windTurbine: View,
    private val pollenView: View,
    private val grassView: View
) {

    private var windSpeedAnim = ObjectAnimator.ofFloat(windTurbine, "rotation", 0f, 360f).apply {
        repeatCount = INFINITE
        interpolator = CustomEasing.straightNoChaser
    }
    private val pollenLevelAnim = ObjectAnimator.ofFloat(pollenView, "alpha", 0f, 1f).apply {
        interpolator = CustomEasing.upRamp
        duration = POLLEN_LEVEL_CHANGE_DURATION_MS
    }
    private val grassAnimX = ObjectAnimator.ofFloat(grassView, "scaleX", 0f, 1.0f).apply {
        interpolator = CustomEasing.overPhlop
        duration = GRASS_ZOOM_DURATION_MS
    }
    private val grassAnimY = ObjectAnimator.ofFloat(grassView, "scaleY", 0f, 1.0f).apply {
        interpolator = CustomEasing.overPhlop
        duration = GRASS_ZOOM_DURATION_MS
    }

    init {
        windTurbine.allowAnimationOutsideParent()
        pollenView.allowAnimationOutsideParent()
        grassView.allowAnimationOutsideParent()
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
        val changePollenAnimSet = AnimatorSet().apply {
            playTogether(
                pollenLevelAnim,
                grassAnimX,
                grassAnimY,
            )
        }
        changePollenAnimSet.start()
    }
}
