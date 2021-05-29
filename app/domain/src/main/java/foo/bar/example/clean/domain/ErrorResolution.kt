package foo.bar.example.clean.domain

import kotlinx.serialization.Serializable

@Serializable
enum class ErrorResolution {
    RETRY_LATER,
    CHECK_NETWORK_THEN_RETRY,
    LOGIN_THEN_RETRY,
}
