package foo.bar.clean.ui.common.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import foo.bar.clean.ui.R
import kotlin.math.min

class PercentPie @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private lateinit var paint: Paint
    private lateinit var backgroundRect: RectF
    private var widthPx = 0f
    private var heightPx = 0f
    private var backgroundColour = 0
    private var progressColour = 0
    private var targetPercent = 0f
    private var currentPercent = -1f
    private val step = 3f

    public override fun onFinishInflate() {
        super.onFinishInflate()
        backgroundColour = resources.getColor(R.color.colorPrimary)
        progressColour = resources.getColor(R.color.colorAccent)
        paint = Paint()
        paint.flags = Paint.ANTI_ALIAS_FLAG
        paint.style = Paint.Style.FILL_AND_STROKE
        backgroundRect = RectF(0f, 0f, widthPx, heightPx)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        widthPx = width.toFloat()
        heightPx = height.toFloat()
        backgroundRect.set(
            0f + paddingLeft,
            0f + paddingTop,
            widthPx - paddingRight,
            heightPx - paddingBottom
        )
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //background
        paint.color = backgroundColour
        canvas.drawArc(backgroundRect, 0f, 360f, true, paint)

        //percent progress
        if (currentPercent > 0) {
            paint.color = progressColour
            canvas.drawArc(backgroundRect, 270f, (currentPercent * 360 / 100), true, paint)
        }
        progressAnimation()
    }

    fun setPercent(percent: Float) {
        targetPercent = percent
        if (currentPercent == -1f) { //don't bother with the animation
            currentPercent = targetPercent
        } else if (targetPercent < currentPercent) { //animate from zero
            currentPercent = 0f
        }
        invalidate()
    }

    private fun progressAnimation() {
        if (currentPercent < targetPercent) {
            currentPercent = min(targetPercent, currentPercent + step)
            invalidate()
        }
    }
}
