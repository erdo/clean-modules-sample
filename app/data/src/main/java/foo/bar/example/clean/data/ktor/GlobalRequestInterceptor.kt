package foo.bar.example.clean.data.ktor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * This will be specific to your own app.
 *
 * Typically you would construct this class with some kind of Session object or similar that
 * you would use to customize the headers according to the logged in status of the user, for example
 */
class GlobalRequestInterceptor(
    /*, private val session: Session */
) : Interceptor {


    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        val original = chain.request()

        val requestBuilder = original.newBuilder()


        requestBuilder.addHeader("Content-Type", "application/json")
        //requestBuilder.addHeader("X-MyApp-Auth-Token", !session.hasSession() ? "expired" : session.getSessionToken());
        requestBuilder.addHeader("User-Agent", "clean-example-user-agent")


        requestBuilder.method(original.method, original.body)

        return chain.proceed(requestBuilder.build())
    }
}
