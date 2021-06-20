package foo.bar.clean.data.api

import foo.bar.clean.domain.ErrorResolution
import foo.bar.clean.domain.ErrorResolution.*

sealed class DataError(val resolution: ErrorResolution) {
    object Misc: DataError(RETRY_LATER)
    object Network: DataError(CHECK_NETWORK_THEN_RETRY)
    object SecurityUnknown: DataError(CHECK_NETWORK_THEN_RETRY)
    object Server: DataError(RETRY_LATER)
    object AlreadyExecuted: DataError(RETRY_LATER)
    object Client: DataError(RETRY_LATER)
    object RateLimited: DataError(RETRY_LATER)
    object SessionTimedOut: DataError(LOGIN_THEN_RETRY)
    object Busy: DataError(RETRY_LATER)
    object LaunchServiceSaysNo: DataError(RETRY_LATER)
    object WeatherUserLocked: DataError(RETRY_LATER)
    object WeatherLoginIncorrect: DataError(LOGIN_THEN_RETRY)

    companion object {
        fun createFromName(label: String?): DataError {
            return when (label) {
                "rate_limit" -> RateLimited
                else -> Misc
            }
        }
    }
}
