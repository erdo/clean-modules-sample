package foo.bar.clean.ui.common.widget

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import foo.bar.clean.ui.R
import foo.bar.clean.ui.common.anim.CustomEasing
import foo.bar.clean.ui.common.anim.allowAnimationOutsideParent
import kotlinx.android.synthetic.main.widget_maxminindicator.view.*

class MaxMinIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val maxAnimSet = AnimatorSet()
    private val minAnimSet = AnimatorSet()
    private var maxObjectAnimator: ObjectAnimator? = null
    private var minObjectAnimator: ObjectAnimator? = null
    private var maxPercent = 0f
    private var minPercent = 0f
    private val animDurationMax: Long = 1000
    private val animDurationMin: Long = 1200

    init {
        inflate(context, R.layout.widget_maxminindicator, this)

        maxmin_max.allowAnimationOutsideParent()
        maxmin_min.allowAnimationOutsideParent()

        maxAnimSet.apply {
            duration = animDurationMax
            interpolator = CustomEasing.hardBounceOut
        }
        minAnimSet.apply {
            duration = animDurationMin
            interpolator = CustomEasing.hardBounceOut
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        reRunAnimations()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        reRunAnimations()
    }

    private fun reRunAnimations() {
        maxObjectAnimator = animateToPercent(minPercent, maxmin_min, minObjectAnimator, minAnimSet)
        maxObjectAnimator = animateToPercent(maxPercent, maxmin_max, maxObjectAnimator, maxAnimSet)
    }

    fun setMinPercent(percent: Float) {
        if (minPercent != percent) {
            minPercent = minOf(maxOf(percent, 0F), 100F)
            minObjectAnimator =
                animateToPercent(minPercent, maxmin_min, minObjectAnimator, minAnimSet)
        }
    }

    fun setMaxPercent(percent: Float) {
        if (maxPercent != percent) {
            maxPercent = minOf(maxOf(percent, 0F), 100F)
            maxObjectAnimator =
                animateToPercent(maxPercent, maxmin_max, maxObjectAnimator, maxAnimSet)
        }
    }

    private fun animateToPercent(
        percent: Float,
        targetView: View,
        animator: ValueAnimator?,
        animatorSet: AnimatorSet
    ): ObjectAnimator? {

        var newAnimator: ObjectAnimator? = null

        if (height != 0 && targetView.height != 0) {

            val currentPosition = animator?.animatedValue?.let {
                if (it is Float) it.toFloat() else 0F
            } ?: 0F
            val targetPosition = -((height - targetView.height) * percent / 100)

            if (targetPosition != currentPosition) {

                newAnimator = ObjectAnimator.ofFloat(
                    targetView,
                    "translationY",
                    currentPosition, targetPosition
                )

                animatorSet.cancel()
                animatorSet.playTogether(
                    newAnimator
                )
                animatorSet.start()
            }
        }

        return newAnimator
    }
}
