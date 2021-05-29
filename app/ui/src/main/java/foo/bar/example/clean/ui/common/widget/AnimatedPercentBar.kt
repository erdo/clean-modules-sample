package foo.bar.example.clean.ui.common.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import foo.bar.example.clean.ui.R
import kotlin.math.max
import kotlin.math.min

@Suppress("DEPRECATION")
class AnimatedPercentBar @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private lateinit var paint: Paint

    private var widthPx: Float = 0.toFloat()
    private var heightPx: Float = 0.toFloat()

    private var backgroundColour: Int = 0
    private var progressColour: Int = 0

    private var targetPercent = 0f
    private var currentPercent = 0f
    private val step = 2f
    private var increasing = false

    private val animateOnTheWayDown = false

    public override fun onFinishInflate() {
        super.onFinishInflate()

        backgroundColour = resources.getColor(R.color.colorPrimary)
        progressColour = resources.getColor(R.color.colorLightGrey)

        paint = Paint()
        paint.flags = Paint.ANTI_ALIAS_FLAG
        paint.style = Paint.Style.FILL_AND_STROKE
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        widthPx = width.toFloat()
        heightPx = height.toFloat()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //background
        paint.color = backgroundColour
        canvas.drawRect(0f, 0f, widthPx, heightPx, paint)

        //percent bar
        paint.color = progressColour
        canvas.drawRect(0f, heightPx/3, currentPercent * widthPx / 100, (heightPx*2)/3, paint)

        progressAnimation()
    }

    fun setPercent(targetPercent: Float) {
        this.targetPercent = maxOf(minOf(targetPercent, 100F), 0F)
        if (currentPercent == 0f) {//don't bother with the animation
            currentPercent = targetPercent
        }
        increasing = targetPercent > currentPercent

        if (!increasing && !animateOnTheWayDown){
            currentPercent = targetPercent //skip animation on the way down
        }

        invalidate()
    }

    private fun progressAnimation() {
        if (increasing && currentPercent < targetPercent) {
            currentPercent = min(targetPercent, currentPercent + step)
            invalidate()
        } else if (!increasing && currentPercent > targetPercent) {
            currentPercent = max(targetPercent, currentPercent - step)
            invalidate()
        }
    }
}
