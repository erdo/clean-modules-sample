package foo.bar.clean.data.api.ktor.services.pollen

import co.early.fore.kt.core.type.Either
import co.early.fore.kt.core.logging.Logger
import co.early.fore.kt.net.ktor.CallWrapperKtor
import foo.bar.clean.data.api.DataError
import foo.bar.clean.data.api.toDomain
import foo.bar.clean.domain.DomainError
import foo.bar.clean.domain.weather.PollenCount
import foo.bar.clean.domain.weather.PollenService

class PollenServiceImp(
    private val client: PollenApi,
    private val wrapper: CallWrapperKtor<DataError>,
    private val logger: Logger,
) : PollenService {

    override suspend fun getPollenCounts(): Either<DomainError, List<PollenCount>> {

        val dataResult = wrapper.processCallAwait {
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
