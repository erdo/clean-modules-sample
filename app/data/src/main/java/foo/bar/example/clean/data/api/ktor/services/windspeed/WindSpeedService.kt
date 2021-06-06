package foo.bar.example.clean.data.api.ktor.services.windspeed

import co.early.fore.kt.core.Either
import co.early.fore.kt.core.logging.Logger
import co.early.fore.kt.net.ktor.CallProcessorKtor
import foo.bar.example.clean.data.api.DataError
import foo.bar.example.clean.data.api.toDomain
import foo.bar.example.clean.domain.ErrorResolution
import foo.bar.example.clean.domain.weather.WindSpeed
import foo.bar.example.clean.domain.weather.WindSpeedService

class WindSpeedServiceImp(
    private val client: WindSpeedApi,
    private val processor: CallProcessorKtor<DataError>,
    private val logger: Logger,
) : WindSpeedService {

    override suspend fun getWindSpeeds(): Either<ErrorResolution, List<WindSpeed>> {

        val dataResult = processor.processCallAwait {
            logger.i("processing call t:" + Thread.currentThread())
            client.getWindSpeedReadings()
        }

        val domainResult = toDomain(dataResult){
            it.map { windSpeedPojo ->
                windSpeedPojo.toDomain()
            }
        }

        return domainResult
    }
}

fun WindSpeedPojo.toDomain(): WindSpeed {
    return WindSpeed(this.windSpeedKmpH)
}
