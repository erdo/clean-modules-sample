package foo.bar.clean.data

import co.early.fore.net.testhelpers.StubbedServiceDefinition
import foo.bar.clean.domain.ErrorResolution
import foo.bar.clean.domain.ErrorResolution.*
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.ArrayList

/**
 * This will be specific to your own app, it's closely linked with: @[CustomGlobalErrorHandler]
 */
class CommonServiceFailures : ArrayList<StubbedServiceDefinition<ErrorResolution>>() {
    init {

        //network down / airplane mode
        add(StubbedServiceDefinition(IOException("fake io exception for testing purposes"), CHECK_NETWORK_THEN_RETRY))

        //network timeout
        add(StubbedServiceDefinition(SocketTimeoutException("fake timeout exception for testing purposes"), CHECK_NETWORK_THEN_RETRY))

        //bad request
        add(StubbedServiceDefinition(400, "common/empty.json", RETRY_LATER))

        //session timeout
        add(StubbedServiceDefinition(401, "common/empty.json", LOGIN_THEN_RETRY))

        //missing resource
        add(StubbedServiceDefinition(404, "common/empty.json", RETRY_LATER))

        //missing resource (html page)
        add(StubbedServiceDefinition(404, "common/html.json", "text/html", RETRY_LATER))

        //bad request
        add(StubbedServiceDefinition(405, "common/empty.json", RETRY_LATER))

        //server down
        add(StubbedServiceDefinition(500, "common/empty.json", RETRY_LATER))

        //service unavailable
        add(StubbedServiceDefinition(503, "common/empty.json", RETRY_LATER))

        //non valid json
        add(StubbedServiceDefinition(200, "common/html.json", RETRY_LATER))
    }
}
