package foo.bar.clean.data.api

import co.early.fore.kt.core.Either
import foo.bar.clean.domain.DomainError

fun <Data, Domain> toDomain(
    dataEither: Either<DataError, Data>,
    toAppBlock: (Data) -> Domain) : Either<DomainError, Domain> {
    return when (dataEither) {
        is Either.Right -> Either.right(toAppBlock(dataEither.b))
        is Either.Left -> Either.left(dataEither.a.resolution)
    }
}
