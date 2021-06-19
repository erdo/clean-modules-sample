package foo.bar.example.clean.ui.dashboard

import foo.bar.example.clean.domain.ErrorResolution
import foo.bar.example.clean.domain.weather.PollenLevel

const val MAX_WIND_SPEED = 60
const val MAX_DIAL_TEMP = 25
const val MIN_DIAL_TEMP = -5

data class DashboardViewState(
    val weather: WeatherViewState = WeatherViewState(),
    val autoRefresh: AutoRefreshViewState = AutoRefreshViewState(),
    val errorResolution: ErrorResolution? = null,
    val isUpdating: Boolean = false,
)

data class WeatherViewState(
    val maxTempC: Int = MIN_DIAL_TEMP,
    val minTempC: Int = MIN_DIAL_TEMP,
    val windSpeedKmpH: Int = 0,
    val pollenLevel: PollenLevel = PollenLevel.UNKNOWN,
) {
    fun maxTempPercent() = valueToPercentage(maxTempC, MIN_DIAL_TEMP, MAX_DIAL_TEMP)
    fun minTempPercent() = valueToPercentage(minTempC, MIN_DIAL_TEMP, MAX_DIAL_TEMP)
    fun windSpeedPercent() = valueToPercentage(windSpeedKmpH, 0, MAX_WIND_SPEED)

    private fun valueToPercentage(value: Int, minValue: Int, maxValue: Int) : Float {
        val boundValue = minOf(maxOf(value, minValue), maxValue)
        val range = maxValue - minValue
        val distanceFromMin = boundValue - minValue
        return (distanceFromMin * 100f)/range
    }
}

data class AutoRefreshViewState(
    val timeElapsedPcent: Float = 0f,
    val autoRefreshing: Boolean = false,
)
