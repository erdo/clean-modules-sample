package foo.bar.clean.data.api

import co.early.fore.kt.core.Either
import foo.bar.clean.domain.ErrorResolution

fun <Data, Domain> toDomain(
    dataEither: Either<DataError, Data>,
    toAppBlock: (Data) -> Domain) : Either<ErrorResolution, Domain> {
    return when (dataEither) {
        is Either.Right -> Either.right(toAppBlock(dataEither.b))
        is Either.Left -> Either.left(dataEither.a.resolution)
    }
}
