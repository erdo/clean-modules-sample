package foo.bar.example.clean.ui.common

import androidx.annotation.StringRes
import foo.bar.example.clean.domain.ErrorResolution
import foo.bar.example.clean.domain.ErrorResolution.*
import foo.bar.example.clean.ui.R

@StringRes fun mapToUserMessage(errorResolution: ErrorResolution): Int {
    return when (errorResolution){
        RETRY_LATER -> R.string.err_retry_later
        CHECK_NETWORK_THEN_RETRY -> R.string.err_network
        LOGIN_THEN_RETRY -> R.string.err_session
    }
}
