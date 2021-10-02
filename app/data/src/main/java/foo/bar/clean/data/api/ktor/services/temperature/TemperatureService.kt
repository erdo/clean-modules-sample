package foo.bar.clean.data.api.ktor.services.temperature

import co.early.fore.kt.core.Either
import co.early.fore.kt.core.logging.Logger
import co.early.fore.kt.net.ktor.CallProcessorKtor
import foo.bar.clean.data.api.DataError
import foo.bar.clean.data.api.toDomain
import foo.bar.clean.domain.DomainError
import foo.bar.clean.domain.weather.Temperature
import foo.bar.clean.domain.weather.TemperatureService

class TemperatureServiceImp(
    private val client: TemperatureApi,
    private val processor: CallProcessorKtor<DataError>,
    private val logger: Logger,
) : TemperatureService {

    override suspend fun getTemperatures(): Either<DomainError, List<Temperature>> {

        val dataResult = processor.processCallAwait {
            logger.i("processing call t:" + Thread.currentThread())
            client.getTemperatureReadings()
        }

        val domainResult = toDomain(dataResult){
            it.map { temperaturePojo ->
                temperaturePojo.toDomain()
            }
        }

        return domainResult
    }
}

fun TemperaturePojo.toDomain(): Temperature {
    return Temperature(
        maxTempC = this.maxTempC,
        minTempC = this.minTempC,
    )
}
