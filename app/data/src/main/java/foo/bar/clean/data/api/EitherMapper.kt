package foo.bar.clean.data.api

import co.early.fore.kt.core.type.Either
import co.early.fore.kt.core.type.Either.Companion.fail
import co.early.fore.kt.core.type.Either.Companion.success
import co.early.fore.kt.core.type.Either.Fail
import co.early.fore.kt.core.type.Either.Success
import foo.bar.clean.domain.DomainError

fun <Data, Domain> toDomain(
    dataEither: Either<DataError, Data>,
    toAppBlock: (Data) -> Domain) : Either<DomainError, Domain> {
    return when (dataEither) {
        is Success -> success(toAppBlock(dataEither.value))
        is Fail -> fail(dataEither.value.resolution)
    }
}
