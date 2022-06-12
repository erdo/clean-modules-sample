package foo.bar.clean.data.api.ktor.services.temperature

import co.early.fore.kt.core.type.Either
import co.early.fore.kt.core.logging.Logger
import co.early.fore.kt.net.ktor.CallWrapperKtor
import foo.bar.clean.data.api.DataError
import foo.bar.clean.data.api.toDomain
import foo.bar.clean.domain.DomainError
import foo.bar.clean.domain.weather.Temperature
import foo.bar.clean.domain.weather.TemperatureService

class TemperatureServiceImp(
    private val client: TemperatureApi,
    private val wrapper: CallWrapperKtor<DataError>,
    private val logger: Logger,
) : TemperatureService {

    override suspend fun getTemperatures(): Either<DomainError, List<Temperature>> {

        val dataResult = wrapper.processCallAwait {
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
