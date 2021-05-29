package foo.bar.example.clean.domain.updater

import co.early.fore.kt.core.logging.Logger
import co.early.fore.core.observer.Observable
import co.early.fore.core.time.SystemTimeWrapper
import co.early.fore.kt.core.coroutine.launchCustom
import co.early.fore.kt.core.observer.ObservableImp
import co.early.persista.PerSista
import kotlinx.coroutines.*
import kotlin.math.max

const val ONE_SECOND_MS: Long = 1000
const val ONE_HOUR_MS: Long = ONE_SECOND_MS * 60 * 60

/**
 * This model periodically calls fetch on the weather model
 *
 * It has a state that indicates the time remaining before the next update
 */
@ExperimentalStdlibApi
class UpdateModel(
    private val updaterUseCase: UpdaterUseCase,
    private val updateIntervalMilliSeconds: Long,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
    private val systemTimeWrapper: SystemTimeWrapper = SystemTimeWrapper(),
    private val perSista: PerSista,
    private val logger: Logger,
) : Observable by ObservableImp(dispatcher = dispatcher, logger = logger) {

    private var job: Job? = null
    private var lastUpdated: Long = 0
    private val updateIntervalSeconds = (updateIntervalMilliSeconds/ONE_SECOND_MS).toInt()

    var currentState = UpdateState(0, updateIntervalSeconds, true)
        private set

    init {
        if (updateIntervalMilliSeconds<1 || updateIntervalMilliSeconds>ONE_HOUR_MS) {
            throw IllegalArgumentException("updateIntervalMilliSeconds must be between 1ms an 1 hour, not:$updateIntervalMilliSeconds ms")
        }
    }

    fun start() {

        logger.i("start()")

        launchCustom(dispatcher) {

            cancelPreviousJob()

            lastUpdated = 0

            job = periodicAsync(ONE_SECOND_MS) {

                val timeRemaining: Long = max(
                    0,
                    lastUpdated + updateIntervalMilliSeconds - systemTimeWrapper.currentTimeMillis()
                ) / ONE_SECOND_MS

                currentState = UpdateState(timeRemaining.toInt(), updateIntervalSeconds, false)
                notifyObservers()

                if (timeRemaining == 0L) {
                    updaterUseCase.execute()
                    // should probably use nanoTime here, but it's not a big deal
                    lastUpdated = systemTimeWrapper.currentTimeMillis()
                }
            }
        }

        currentState = currentState.copy(timeToNextUpdateSeconds = updateIntervalSeconds)
        notifyObservers()
    }

    fun stop(){
        logger.i("stop()")

        currentState = UpdateState(0, updateIntervalSeconds, true)
        notifyObservers()

        launchCustom(dispatcher) {
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
