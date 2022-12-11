package foo.bar.clean.domain.refresher

import co.early.fore.core.time.SystemTimeWrapper
import co.early.fore.kt.core.coroutine.launchMain
import co.early.fore.kt.core.logging.Logger
import foo.bar.clean.domain.common.StateModel
import foo.bar.clean.domain.common.StateModelImp
import foo.bar.clean.domain.common.UdfModel
import kotlinx.coroutines.*
import kotlin.math.max

const val ONE_HOUR_S: Int = 60 * 60
const val ONE_SECOND_MS: Long = 1000

/**
 * This model periodically calls refresh
 *
 * It has a state that indicates the time remaining before the next refresh
 */
class RefreshModel(
    private val refresh: () -> Unit,
    private val refreshIntervalSeconds: Int,
    private val systemTimeWrapper: SystemTimeWrapper = SystemTimeWrapper(),
    private val logger: Logger,
) : UdfModel<RefreshState, RefreshAction>, StateModel<RefreshState> by StateModelImp(
    initialState = RefreshState(refreshIntervalSeconds = refreshIntervalSeconds)
) {

    private var job: Job? = null
    private var lastUpdatedMs: Long = 0

    init {
        require(refreshIntervalSeconds in 1..ONE_HOUR_S) {
            refresh
            throw IllegalArgumentException("updateIntervalMilliSeconds must be between 1s an 1 hour, not:$refreshIntervalSeconds s")
        }
    }

    override fun send(action: RefreshAction) {
        when (action) {
            RefreshAction.Start -> start()
            RefreshAction.Stop -> stop()
        }
    }

    private fun start() {

        logger.i("start()")

        /**
         * See the fore sample apps for how to test this code - it's easy.
         * Or use an unwrapped coroutine here if you have a preferred solution
         *
         * The only thing that matters is that we update the state when it changes
         * (which will result in all the observers being informed)
         */
        launchMain {

            cancelPreviousJob()

            lastUpdatedMs = 0
            var firstRun = true

            job = periodicAsync(ONE_SECOND_MS) {

                val timeRemainingS: Long = max(
                    0,
                    lastUpdatedMs + (refreshIntervalSeconds * ONE_SECOND_MS) - systemTimeWrapper.currentTimeMillis()
                ) / ONE_SECOND_MS

                updateState {
                    RefreshState(
                        timeToNextRefreshSeconds = if (firstRun) refreshIntervalSeconds else timeRemainingS.toInt(),
                        refreshIntervalSeconds = refreshIntervalSeconds,
                        paused = false
                    )
                }

                firstRun = false

                if (timeRemainingS == 0L) {
                    refresh()
                    lastUpdatedMs = systemTimeWrapper.currentTimeMillis()
                }
            }
        }
    }

    private fun stop() {

        logger.i("stop()")

        launchMain {
            cancelPreviousJob()
        }

        updateState {
            copy(timeToNextRefreshSeconds = 0, paused = true)
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
        periodMillis: Long, block: () -> Unit
    ) = this.async {
        while (isActive) {
            block()
            delay(periodMillis)
        }
    }
}
