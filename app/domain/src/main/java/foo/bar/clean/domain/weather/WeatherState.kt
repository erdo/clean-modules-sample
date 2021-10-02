package foo.bar.clean.domain.weather

import foo.bar.clean.domain.DomainError
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class WeatherState(
    val weatherReport: WeatherReport = WeatherReport(),
    val error: DomainError? = null,
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
    val maxTempC: Int? = null,
    val minTempC: Int? = null,
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
