package foo.bar.example.clean.ui.common

import android.content.Context
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import foo.bar.example.clean.domain.ErrorResolution
import foo.bar.example.clean.domain.weather.PollenLevel
import foo.bar.example.clean.ui.R

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.showToast(message: ErrorResolution) {
    Toast.makeText(this, message.mapToUserMessage(), Toast.LENGTH_LONG).show()
}

@StringRes
fun ErrorResolution.mapToUserMessage(): Int {
    return when (this){
        ErrorResolution.RETRY_LATER -> R.string.err_retry_later
        ErrorResolution.CHECK_NETWORK_THEN_RETRY -> R.string.err_network
        ErrorResolution.LOGIN_THEN_RETRY -> R.string.err_session
    }
}

@DrawableRes
fun PollenLevel.toImgRes(): Int {
    return when (this) {
        PollenLevel.HIGH -> R.drawable.pollen_high
        PollenLevel.MEDIUM -> R.drawable.pollen_medium
        PollenLevel.LOW -> R.drawable.pollen_low
        PollenLevel.UNKNOWN -> 0
    }
}

// https://gist.github.com/Mayankmkh/92084bdf2b59288d3e74c3735cccbf9f
fun Any.prettyPrint(): String {

    var indentLevel = 0
    val indentWidth = 4

    fun padding() = "".padStart(indentLevel * indentWidth)

    val toString = toString()

    val stringBuilder = StringBuilder(toString.length)

    var i = 0
    while (i < toString.length) {
        when (val char = toString[i]) {
            '(', '[', '{' -> {
                indentLevel++
                stringBuilder.appendLine(char).append(padding())
            }
            ')', ']', '}' -> {
                indentLevel--
                stringBuilder.appendLine().append(padding()).append(char)
            }
            ',' -> {
                stringBuilder.appendLine(char).append(padding())
                // ignore space after comma as we have added a newline
                val nextChar = toString.getOrElse(i + 1) { char }
                if (nextChar == ' ') i++
            }
            else -> {
                stringBuilder.append(char)
            }
        }
        i++
    }

    return stringBuilder.toString()
}
