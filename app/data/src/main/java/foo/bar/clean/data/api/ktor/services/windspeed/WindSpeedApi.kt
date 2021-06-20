package foo.bar.clean.data.api.ktor.services.windspeed

import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable

/**
 * These stubs are hosted at https://www.mocky.io/
 *
 * success example response:
 * https://run.mocky.io/v3/60e00580-b683-4193-9cb3-856743f7863a
 *
 */
data class WindSpeedApi(
    val getWindSpeedReadings: suspend () -> List<WindSpeedPojo>
) {
    companion object {
        fun create(httpClient: HttpClient): WindSpeedApi {
            val baseUrl = "https://run.mocky.io/v3/"
            return WindSpeedApi(
                getWindSpeedReadings = { httpClient.get("${baseUrl}60e00580-b683-4193-9cb3-856743f7863a/?mocky-delay=1s") },
            )
        }
    }
}

/**
 *
 *
 * <Code>
 *
 * The server returns us a list of wind speed readings that look like this:
 *
 * {
 * "windSpeedKmpH":15
 * }
 *
 * </Code>
 *
 *
 */
@Serializable
data class WindSpeedPojo(
    val windSpeedKmpH: Int = 35,
)
