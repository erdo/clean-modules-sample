package foo.bar.clean.domain.weather

import co.early.fore.kt.core.coroutine.awaitMain
import co.early.fore.kt.core.coroutine.launchIO
import co.early.fore.kt.core.logging.Logger
import co.early.fore.kt.core.type.Either
import co.early.fore.kt.core.type.Either.Companion.success
import co.early.fore.kt.core.type.Either.Fail
import co.early.fore.kt.core.type.Either.Success
import co.early.fore.kt.core.type.carryOn
import co.early.persista.PerSista
import foo.bar.clean.domain.DomainError
import foo.bar.clean.domain.Randomizer
import foo.bar.clean.domain.common.ActionReceiver
import foo.bar.clean.domain.common.PersistentStateModelImp
import foo.bar.clean.domain.common.StateModel
import foo.bar.clean.domain.common.UdfModel
import kotlin.reflect.full.createType

interface PollenService {
    suspend fun getPollenCounts(): Either<DomainError, List<PollenCount>>
}

interface TemperatureService {
    suspend fun getTemperatures(): Either<DomainError, List<Temperature>>
}

interface WindSpeedService {
    suspend fun getWindSpeeds(): Either<DomainError, List<WindSpeed>>
}

/**
 * This model makes 3 network connections to a static server to fetch: a list of pollen counts,
 * a list of temperatures, a list of windSpeeds (for each list, one item is chosen at random to
 * make the UI interesting).
 *
 * Each network connection returns an Either<Error, Data> the carryOn() function only continues if
 * the previous connection was a success, if the previous connection failed, then processing stops
 * and the Either<Error> is past back up to the result.
 *
 * This one is backed by Ktor for network comms (see the data layer)
 */
class WeatherModel(
    private val pollenService: PollenService,
    private val temperatureService: TemperatureService,
    private val windSpeedService: WindSpeedService,
    private val perSista: PerSista,
    private val logger: Logger,
) : UdfModel<WeatherState, WeatherAction>, StateModel<WeatherState> by PersistentStateModelImp(
    WeatherState(),
    WeatherState::class.createType(),
    perSista
) {

    override fun send(action: WeatherAction) {
        when (action) {
            WeatherAction.FetchWeatherReport -> fetchWeatherReport()
        }
    }

    /**
     * fetch weather reports using Ktor
     */
    private fun fetchWeatherReport() {

        logger.i("fetchWeatherReport() thread:" + Thread.currentThread().id)

        if (currentState().isUpdating) {
            return
        }

        updateState { copy(isUpdating = true, error = null) }

        /**
         * See the fore sample apps for how to test this code - it's easy.
         * Or use an unwrapped coroutine here if you have a preferred solution
         * and/or aren't using fore
         *
         * What matters is we fetch our data, and update the state once we're done
         */
        launchIO {

            logger.i("in scope from Dispatchers.IO, thread:" + Thread.currentThread().id)

            var partialWeatherReport = WeatherReport()

            val weatherReport = pollenService.getPollenCounts()
                .carryOn { pollenCounts ->
                    logger.i("received pollenCounts success")
                    partialWeatherReport = partialWeatherReport.copy(
                        pollenCount = Randomizer.choose(pollenCounts) ?: PollenCount()
                    )
                    temperatureService.getTemperatures()
                }
                .carryOn { temperatures ->
                    logger.i("received temperatures success")
                    partialWeatherReport = partialWeatherReport.copy(
                        temperature = Randomizer.choose(temperatures) ?: Temperature()
                    )
                    windSpeedService.getWindSpeeds()
                }
                .carryOn { windSpeeds ->
                    logger.i("received windSpeeds success")
                    partialWeatherReport = partialWeatherReport.copy(
                        windSpeed = Randomizer.choose(windSpeeds) ?: WindSpeed()
                    )
                    success(partialWeatherReport)
                }

            logger.i("requests are all complete, thread:" + Thread.currentThread().id)

            val newState = when (weatherReport) {
                is Success -> WeatherState(weatherReport.value, null, false)
                is Fail -> WeatherState(error = weatherReport.value, isUpdating = false)
            }

            awaitMain {
                updateState { newState }
            }
        }
    }
}
