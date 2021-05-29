package foo.bar.example.clean.data.ktor.services.pollen

import co.early.fore.kt.core.Either
import co.early.fore.kt.core.logging.Logger
import co.early.fore.kt.net.ktor.CallProcessorKtor
import foo.bar.example.clean.data.DataError
import foo.bar.example.clean.data.toDomain
import foo.bar.example.clean.domain.ErrorResolution
import foo.bar.example.clean.domain.weather.PollenCount
import foo.bar.example.clean.domain.weather.PollenService

class PollenServiceImp(
    private val client: PollenApi,
    private val processor: CallProcessorKtor<DataError>,
    private val logger: Logger,
) : PollenService {

    override suspend fun getPollenCounts(): Either<ErrorResolution, List<PollenCount>> {

        val dataResult = processor.processCallAwait {
            logger.i("processing call t:" + Thread.currentThread())
            client.getPollenCountReadings()
        }

        val domainResult = toDomain(dataResult){
            it.map { pollenPojo ->
                pollenPojo.toDomain()
            }
        }

        return domainResult
    }
}

fun PollenPojo.toDomain(): PollenCount {
    return PollenCount(this.level.domainPollenLevel)
}
