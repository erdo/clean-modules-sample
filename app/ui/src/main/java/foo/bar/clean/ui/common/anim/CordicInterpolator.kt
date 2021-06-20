package foo.bar.clean.ui.common.anim

import android.animation.TimeInterpolator

const val ONE_THOUSAND = 1000
const val ZERO = 0

/**
 * @param cordicTable The value at [0] represents the start of the animation and must
 * be = 0, the value at [size-1] represents the end of the animation and must be 1000.
 * The values in between are up to you and will create the easing effect when applied
 * to an animation. Values above 1000 or below 0 are valid for easing styles that
 * overshoot or undershoot.
 *
 * Recommended size for the cordic table is 1000 elements or more, fewer will still
 * work but can start to look jerky when applied to a large animation.
 */
open class CordicInterpolator(
    private val cordicTable: List<Int>
) : TimeInterpolator {

    init {
        check(cordicTable[ZERO] == ZERO){
            "the first value of the cordicTable must be $ZERO (not ${cordicTable[ZERO]})"
        }
        check(cordicTable[cordicTable.size-1] == ONE_THOUSAND){
            "the last value of the cordicTable must be $ONE_THOUSAND (not ${cordicTable[cordicTable.size-1]})"
        }
    }

    override fun getInterpolation(input: Float): Float {
        return cordicTable[(input * (cordicTable.size-1)).toInt()].toFloat() / ONE_THOUSAND
    }
}
