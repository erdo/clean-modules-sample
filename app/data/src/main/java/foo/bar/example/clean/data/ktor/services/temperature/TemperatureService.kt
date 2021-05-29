package foo.bar.example.clean.data.ktor.services.temperature

import co.early.fore.kt.core.Either
import co.early.fore.kt.core.logging.Logger
import co.early.fore.kt.net.ktor.CallProcessorKtor
import foo.bar.example.clean.data.DataError
import foo.bar.example.clean.data.toDomain
import foo.bar.example.clean.domain.ErrorResolution
import foo.bar.example.clean.domain.weather.Temperature
import foo.bar.example.clean.domain.weather.TemperatureService

class TemperatureServiceImp(
    private val client: TemperatureApi,
    private val processor: CallProcessorKtor<DataError>,
    private val logger: Logger,
) : TemperatureService {

    override suspend fun getTemperatures(): Either<ErrorResolution, List<Temperature>> {

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
