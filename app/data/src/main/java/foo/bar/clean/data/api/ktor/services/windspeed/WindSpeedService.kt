package foo.bar.clean.data.api.ktor.services.windspeed

import co.early.fore.kt.core.type.Either
import co.early.fore.kt.core.logging.Logger
import co.early.fore.kt.net.ktor.CallWrapperKtor
import foo.bar.clean.data.api.DataError
import foo.bar.clean.data.api.toDomain
import foo.bar.clean.domain.DomainError
import foo.bar.clean.domain.weather.WindSpeed
import foo.bar.clean.domain.weather.WindSpeedService

class WindSpeedServiceImp(
    private val client: WindSpeedApi,
    private val wrapper: CallWrapperKtor<DataError>,
    private val logger: Logger,
) : WindSpeedService {

    override suspend fun getWindSpeeds(): Either<DomainError, List<WindSpeed>> {

        val dataResult = wrapper.processCallAwait {
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
