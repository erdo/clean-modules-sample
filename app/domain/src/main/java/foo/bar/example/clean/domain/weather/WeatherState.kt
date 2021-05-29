package foo.bar.example.clean.domain.weather

import foo.bar.example.clean.domain.ErrorResolution
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class WeatherState(
    val weatherReport: WeatherReport = WeatherReport(),
    val error: ErrorResolution? = null,
    @Transient
    val isUpdating: Boolean = false,
)

@Serializable
data class WeatherReport(
    val pollenCount: PollenCount = PollenCount(),
    val temperature: Temperature = Temperature(),
    val windSpeed: WindSpeed = WindSpeed(),
)

@Serializable
data class PollenCount(
    val pollenLevel: PollenLevel = PollenLevel.UNKNOWN,
)

@Serializable
data class Temperature(
    val maxTempC: Int = 35,
    val minTempC: Int = 10,
)

@Serializable
data class WindSpeed(
    val windSpeedKmpH: Int = 0,
)

@Serializable
enum class PollenLevel {
    HIGH,
    MEDIUM,
    LOW,
    UNKNOWN,
}
