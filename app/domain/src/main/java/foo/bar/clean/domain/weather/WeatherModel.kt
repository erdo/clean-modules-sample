package foo.bar.clean.domain.weather

import co.early.fore.kt.core.logging.Logger
import co.early.fore.core.observer.Observable
import co.early.fore.kt.core.Either
import co.early.fore.kt.core.Either.Left
import co.early.fore.kt.core.Either.Right
import co.early.fore.kt.core.Error
import co.early.fore.kt.core.Success
import co.early.fore.kt.core.carryOn
import co.early.fore.kt.core.coroutine.*
import co.early.fore.kt.core.observer.ObservableImp
import co.early.persista.PerSista
import foo.bar.clean.domain.Randomizer
import foo.bar.clean.domain.DomainError

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
@OptIn(ExperimentalStdlibApi::class)
class WeatherModel(
    private val pollenService: PollenService,
    private val temperatureService: TemperatureService,
    private val windSpeedService: WindSpeedService,
    private val perSista: PerSista,
    private val logger: Logger,
) : Observable by ObservableImp() {

    var currentState = WeatherState(isUpdating = false)
        private set

    init {
        perSista.read(currentState) { readState ->
            currentState = readState
            notifyObservers()
        }
    }

    /**
     * fetch weather reports using Ktor
     */
    fun fetchWeatherReport() {

        logger.i("fetchWeatherReport() thread:" + Thread.currentThread().id)

        if (currentState.isUpdating) {
            return
        }

        currentState = currentState.copy(isUpdating = true, error = null)
        notifyObservers()

        /**
         * See the fore sample apps for how to test this code - it's easy.
         * Or use an unwrapped coroutine here if you have a preferred solution
         * and/or aren't using fore
         */
        launchIO {

            logger.i("in scope from Dispatchers.IO, thread:" + Thread.currentThread().id)

            var partialWeatherReport = WeatherReport()

            /**
             * that carryOn() extension function has nothing to do with the architecture
             * of this sample btw, you can do whatever you like here, including using
             * reactive streams if appropriate, but when you want to expose the calculated
             * state, you need to: 1) set it, and 2) call notifyObservers().
             *
             * (If you're interested, carryOn() lets you transparently handle networking
             * errors at each step - we make 3 network connections in the following code.
             * Internet search for "railway oriented programming" and "andThen" functions)
             */
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
                    eitherSuccess(partialWeatherReport)
                }

            logger.i("requests are all complete, thread:" + Thread.currentThread().id)

            val newState = when (weatherReport) {
                is Success -> WeatherState(weatherReport.b, null, false)
                is Error -> WeatherState(error = weatherReport.a, isUpdating = false)
            }

            perSista.write(newState) { saved ->
                logger.i("after writing to disk, perSista returns to us on the UI thread:" + Thread.currentThread().id)
                currentState = saved
                notifyObservers()
            }

            /**
             * if we weren't using perSista to save our state, we would need to
             * hop to the UI thread manually to safely update our state
             */
//            awaitMain {
//                logger.i("jump to the UI thread to update our state, thread:" + Thread.currentThread().id)
//                currentState = newState
//                notifyObservers()
//            }
        }
    }
}

fun <R> eitherSuccess(value: R): Either<Nothing, R> = Either.right(value)
fun <L> eitherError(value: L): Either<L, Nothing> = Either.left(value)