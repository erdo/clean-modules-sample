package foo.bar.clean.domain.refresher

import foo.bar.clean.domain.common.State

data class RefreshState (
    val timeToNextRefreshSeconds: Int = 0,
    val refreshIntervalSeconds: Int = 0,
    val paused: Boolean = true,
): State {
    fun percentElapsedToNextRefresh(): Float {
        return if (paused || (refreshIntervalSeconds == timeToNextRefreshSeconds) || refreshIntervalSeconds == 0){
            0f
        } else {
            100 * ((refreshIntervalSeconds - timeToNextRefreshSeconds)).toFloat() / refreshIntervalSeconds
        }
    }
}
