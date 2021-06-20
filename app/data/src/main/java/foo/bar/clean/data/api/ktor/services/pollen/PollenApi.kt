package foo.bar.clean.data.api.ktor.services.pollen

import foo.bar.clean.domain.weather.PollenLevel
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable

/**
 * These stubs are hosted at https://www.mocky.io/
 *
 * success example response:
 * https://run.mocky.io/v3/2c43bf76-1a3b-4601-953f-ab4b6a68c6e0
 *
 */
data class PollenApi(
    val getPollenCountReadings: suspend () -> List<PollenPojo>
) {
    companion object {
        fun create(httpClient: HttpClient): PollenApi {
            val baseUrl = "https://run.mocky.io/v3/"
            return PollenApi(
                getPollenCountReadings = { httpClient.get("${baseUrl}2c43bf76-1a3b-4601-953f-ab4b6a68c6e0/?mocky-delay=1s") },
            )
        }
    }
}

/**
 *
 *
 * <Code>
 *
 * The server returns us a list of pollen counts that look like this:
 *
 * {
 * "level":"MEDIUM"
 * }
 *
 * </Code>
 *
 *
 */
@Serializable
data class PollenPojo(
    var level: Level = Level.UNKNOWN
)

enum class Level (val domainPollenLevel: PollenLevel) {
    HIGH(PollenLevel.HIGH),
    MEDIUM(PollenLevel.MEDIUM),
    LOW(PollenLevel.LOW),
    UNKNOWN(PollenLevel.UNKNOWN),
}
