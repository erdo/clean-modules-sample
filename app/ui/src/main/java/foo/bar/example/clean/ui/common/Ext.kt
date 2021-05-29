package foo.bar.example.clean.ui.common

import android.content.Context
import android.widget.Toast
import foo.bar.example.clean.domain.ErrorResolution

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.showToast(message: ErrorResolution) {
    Toast.makeText(this, message.name, Toast.LENGTH_LONG).show()
}