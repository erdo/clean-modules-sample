package foo.bar.clean.domain.refresher

data class RefreshState(
    val timeToNextUpdateSeconds: Int,
    val updateIntervalSeconds: Int,
    val updatesPaused: Boolean
) {
    fun percentElapsedToNextUpdate(): Float {
        return if (updatesPaused || (updateIntervalSeconds == timeToNextUpdateSeconds) || updateIntervalSeconds == 0){
            0f
        } else {
            100 * ((updateIntervalSeconds - timeToNextUpdateSeconds)).toFloat() / updateIntervalSeconds
        }
    }
}
