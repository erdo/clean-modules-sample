package foo.bar.clean.ui.common.anim

import android.animation.TimeInterpolator
import android.view.animation.Interpolator
import androidx.core.view.animation.PathInterpolatorCompat

object CustomEasing {
    // you can design your own easing paths here: https://matthewlein.com/tools/ceaser
    val upRamp: Interpolator = PathInterpolatorCompat.create(0.390f, 0.025f, 0.960f, 0.570f)
    val straightNoChaser: Interpolator = PathInterpolatorCompat.create(0.250f, 0.250f, 0.750f, 0.750f)
    val overPhlop: Interpolator = PathInterpolatorCompat.create(0.370f, 0.870f, 0.755f, 1.420f)
    val hardBounceOut: TimeInterpolator = CordicBounceOut()
}
