package foo.bar.clean.data.api.ktor.services.temperature

import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable

/**
 * These stubs are hosted at https://www.mocky.io/
 *
 * success example response:
 * https://run.mocky.io/v3/a24a6aca-7880-448c-8824-07449d871ddd
 *
 */
data class TemperatureApi(
    val getTemperatureReadings: suspend () -> List<TemperaturePojo>
) {
    companion object {
        fun create(httpClient: HttpClient): TemperatureApi {
            val baseUrl = "https://run.mocky.io/v3/"
            return TemperatureApi(
                getTemperatureReadings = { httpClient.get("${baseUrl}a24a6aca-7880-448c-8824-07449d871ddd/?mocky-delay=1s") },
            )
        }
    }
}

/**
 *
 *
 * <Code>
 *
 * The server returns us a list of temperature readings that look like this:
 *
 * {
 * "maxTempC":35
 * "minTempC":10
 * }
 *
 * </Code>
 *
 *
 */
@Serializable
data class TemperaturePojo(
    val maxTempC: Int = 35,
    val minTempC: Int = 10,
)
