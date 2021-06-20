package foo.bar.clean.ui.common.anim

import android.os.Build
import android.view.View
import android.view.ViewGroup

fun View.allowAnimationOutsideParent() {
    var v = this
    while (v.parent != null && v.parent is ViewGroup) {
        val viewGroup = v.parent as ViewGroup
        viewGroup.clipChildren = false
        viewGroup.clipToPadding = false
        if (Build.VERSION.SDK_INT > 20) {
            viewGroup.clipToOutline = false
        }
        v = viewGroup
    }
}
