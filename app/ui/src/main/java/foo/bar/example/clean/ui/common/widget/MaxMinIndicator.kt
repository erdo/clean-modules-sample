//package foo.bar.example.clean.ui.common.widget
//
//import android.animation.ObjectAnimator
//import android.content.Context
//import android.graphics.Canvas
//import android.util.AttributeSet
//import android.widget.FrameLayout
//import foo.bar.example.clean.ui.R
//import kotlinx.android.synthetic.main.widget_maxminindicator.view.*
//import kotlin.math.max
//import kotlin.math.min
//
//@Suppress("DEPRECATION")
//class MaxMinIndicator @JvmOverloads constructor(
//        context: Context,
//        attrs: AttributeSet? = null,
//        defStyleAttr: Int = 0
//) : FrameLayout(context, attrs, defStyleAttr) {
//
//    init {
//        inflate(context, R.layout.activity_dashboard, this)
//    }
//
//    private var widthPx: Float = 0.toFloat()
//    private var heightPx: Float = 0.toFloat()
//
//    private var maxAnimator : ObjectAnimator? = null
//    private var minAnimator : ObjectAnimator? = null
//
//    private var maxPercent = 0f
//    private var minPercent = 0f
//
//
//    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
//        super.onSizeChanged(w, h, oldw, oldh)
//        widthPx = width.toFloat()
//        heightPx = height.toFloat()
//        invalidate()
//    }
//
//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//
//        //background
//        paint.color = backgroundColour
//        canvas.drawRect(0f, 0f, widthPx, heightPx, paint)
//
//        //percent bar
//        paint.color = progressColour
//        canvas.drawRect(0f, heightPx/3, currentPercent * widthPx / 100, (heightPx*2)/3, paint)
//
//        progressAnimation()
//    }
//
//    fun setPercent(targetPercent: Float) {
//        this.targetPercent = maxOf(minOf(targetPercent, 100F), 0F)
//        if (currentPercent == 0f) {//don't bother with the animation
//            currentPercent = targetPercent
//        }
//        increasing = targetPercent > currentPercent
//
//        if (!increasing && !animateOnTheWayDown){
//            currentPercent = targetPercent //skip animation on the way down
//        }
//
//        invalidate()
//    }
//
//    private fun progressAnimation() {
//
//        maxAnimator = ObjectAnimator.ofFloat(maxmin_max_img, "translationX", -heightPx / 2f, 0f)
//        maxAnimator?.interpolator = CustomEasing.bounceDown
//
//
//
//        if (increasing && currentPercent < targetPercent) {
//            currentPercent = min(targetPercent, currentPercent + step)
//            invalidate()
//        } else if (!increasing && currentPercent > targetPercent) {
//            currentPercent = max(targetPercent, currentPercent - step)
//            invalidate()
//        }
//    }
//}
