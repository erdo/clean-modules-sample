package foo.bar.clean.domain

import java.util.*

/**
 * To make these examples not always show identical data on the UI (most of the APIs
 * we use return static lists), and then we select one at random here
 */
class Randomizer {
    companion object {
        private val random = Random()
        fun <T> choose(things: List<T>): T? {
            return when {
                things.isEmpty() -> null
                things.size == 1 -> things[0]
                else -> things[random.nextInt(things.size - 1)]
            }
        }
    }
}
