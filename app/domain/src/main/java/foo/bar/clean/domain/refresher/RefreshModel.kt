package foo.bar.clean.domain.refresher

import co.early.fore.kt.core.logging.Logger
import co.early.fore.core.observer.Observable
import co.early.fore.core.time.SystemTimeWrapper
import co.early.fore.kt.core.coroutine.launchMain
import co.early.fore.kt.core.observer.ObservableImp
import kotlinx.coroutines.*
import kotlin.math.max

const val ONE_SECOND_MS: Long = 1000
const val ONE_HOUR_MS: Long = ONE_SECOND_MS * 60 * 60

/**
 * This model periodically calls fetch on the weather model
 *
 * It has a state that indicates the time remaining before the next update
 */
@OptIn(ExperimentalStdlibApi::class)
class RefreshModel constructor(
    private val onRefreshMediator: OnRefreshMediator,
    private val refreshIntervalMilliSeconds: Long,
    private val systemTimeWrapper: SystemTimeWrapper = SystemTimeWrapper(),
    private val logger: Logger,
) : Observable by ObservableImp() {

    private var job: Job? = null
    private var lastUpdated: Long = 0
    private val updateIntervalSeconds = (refreshIntervalMilliSeconds/ ONE_SECOND_MS).toInt()

    var currentState = RefreshState(0, updateIntervalSeconds, true)
        private set

    init {
        if (refreshIntervalMilliSeconds<1 || refreshIntervalMilliSeconds> ONE_HOUR_MS) {
            throw IllegalArgumentException("updateIntervalMilliSeconds must be between 1ms an 1 hour, not:$refreshIntervalMilliSeconds ms")
        }
    }

    fun start() {

        logger.i("start()")

        /**
         * See the fore sample apps for how to test this code - it's easy.
         * Or use an unwrapped coroutine here if you have a preferred solution
         */
        launchMain {

            cancelPreviousJob()

            lastUpdated = 0
            var firstRun = true

            job = periodicAsync(ONE_SECOND_MS) {

                val timeRemaining: Long = max(
                    0,
                    lastUpdated + refreshIntervalMilliSeconds - systemTimeWrapper.currentTimeMillis()
                ) / ONE_SECOND_MS

                currentState = RefreshState(
                    timeToNextUpdateSeconds = if (firstRun) updateIntervalSeconds else timeRemaining.toInt(),
                    updateIntervalSeconds = updateIntervalSeconds,
                    updatesPaused = false)
                notifyObservers()

                firstRun = false

                if (timeRemaining == 0L) {
                    onRefreshMediator.refreshNow()
                    lastUpdated = systemTimeWrapper.currentTimeMillis()
                }
            }
        }

        currentState = currentState.copy(timeToNextUpdateSeconds = updateIntervalSeconds)
        notifyObservers()
    }

    fun stop(){
        logger.i("stop()")

        currentState = RefreshState(0, updateIntervalSeconds, true)
        notifyObservers()

        launchMain {
            cancelPreviousJob()
        }
    }

    private suspend fun cancelPreviousJob() {

        logger.i("cancelPreviousJob()")

        job?.let {
            if (it.isActive) {
                it.cancelAndJoin()
            }
        }
    }

    private fun CoroutineScope.periodicAsync(
        periodMillis: Long,
        block: () -> Unit
    ) = this.async {
        while (isActive) {
            block()
            delay(periodMillis)
        }
    }
}
