package foo.bar.example.clean.data.ktor.services.windspeed

import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable

/**
 * These stubs are hosted at https://www.mocky.io/
 *
 * success example response:
 * https://run.mocky.io/v3/aa9bbd20-0ca6-479d-91f2-e23f89140dba
 *
 */
data class WindSpeedApi(
    val getWindSpeedReadings: suspend () -> List<WindSpeedPojo>
) {
    companion object {
        fun create(httpClient: HttpClient): WindSpeedApi {
            val baseUrl = "https://run.mocky.io/v3/"
            return WindSpeedApi(
                getWindSpeedReadings = { httpClient.get("${baseUrl}aa9bbd20-0ca6-479d-91f2-e23f89140dba/?mocky-delay=2s") },
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
