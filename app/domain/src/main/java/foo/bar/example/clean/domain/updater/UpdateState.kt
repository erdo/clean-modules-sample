package foo.bar.example.clean.domain.updater

data class UpdateState(
    val timeToNextUpdateSeconds: Int,
    val updateIntervalSeconds: Int,
    val updatesPaused: Boolean
) {
    fun percentElapsedToNextUpdate(): Float {
        return if ((updateIntervalSeconds == timeToNextUpdateSeconds) || updateIntervalSeconds == 0){
            0f
        } else {
            100 * ((updateIntervalSeconds - timeToNextUpdateSeconds)).toFloat() / updateIntervalSeconds
        }
    }
}
