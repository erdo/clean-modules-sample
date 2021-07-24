package foo.bar.clean.domain.weather

import co.early.fore.kt.core.logging.Logger
import co.early.fore.core.observer.Observable
import co.early.fore.kt.core.Either
import co.early.fore.kt.core.Either.Left
import co.early.fore.kt.core.Either.Right
import co.early.fore.kt.core.carryOn
import co.early.fore.kt.core.coroutine.launchCustom
import co.early.fore.kt.core.observer.ObservableImp
import co.early.persista.PerSista
import foo.bar.clean.domain.Randomizer
import foo.bar.clean.domain.ErrorResolution
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface PollenService {
    suspend fun getPollenCounts(): Either<ErrorResolution, List<PollenCount>>
}

interface TemperatureService {
    suspend fun getTemperatures(): Either<ErrorResolution, List<Temperature>>
}

interface WindSpeedService {
    suspend fun getWindSpeeds(): Either<ErrorResolution, List<WindSpeed>>
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
@ExperimentalStdlibApi
class WeatherModel(
    private val pollenService: PollenService,
    private val temperatureService: TemperatureService,
    private val windSpeedService: WindSpeedService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
    private val perSista: PerSista,
    private val logger: Logger,
) : Observable by ObservableImp(dispatcher = dispatcher, logger = logger) {

    var currentState = WeatherState(isUpdating = false)
        private set

    init {
        perSista.read(currentState) {
            currentState = it
            notifyObservers()
        }
    }

    /**
     * fetch weather reports using Ktor
     */
    fun fetchWeatherReport() {

        logger.i("fetchWeatherReport()")

        if (currentState.isUpdating) {
            return
        }

        currentState = currentState.copy(isUpdating = true, error = null)
        notifyObservers()

        launchCustom(dispatcher) {

            var partialWeatherReport = WeatherReport()

            // that carryOn() extension function has nothing to do with the architecture
            // of this sample btw, you can do whatever you like here, including using
            // reactive streams if appropriate, but when you want to expose the calculated
            // state, you need to: 1) set it, and 2) call notifyObservers().
            // (If you're interested, the carryOn extension function lets you transparently
            // handle networking errors at each step, internet search for "andThen"
            // functions and "railway oriented programming")
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
                    Either.right(partialWeatherReport)
                }

            perSista.write(
                when (weatherReport) {
                    is Right -> WeatherState(weatherReport.b, null, false)
                    is Left -> WeatherState(error = weatherReport.a, isUpdating = false)
                }
            ) {
                currentState = it
                notifyObservers()
            }
        }
    }
}
