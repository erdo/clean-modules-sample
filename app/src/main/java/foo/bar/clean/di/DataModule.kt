package foo.bar.clean.di

import co.early.fore.core.time.SystemTimeWrapper
import co.early.fore.kt.core.delegate.ForeDelegateHolder
import co.early.fore.kt.net.InterceptorLogging
import co.early.fore.kt.net.ktor.CallProcessorKtor
import co.early.persista.PerSista
import foo.bar.clean.App
import foo.bar.clean.data.api.ktor.CustomKtorBuilder
import foo.bar.clean.data.api.ktor.ErrorHandler
import foo.bar.clean.data.api.ktor.GlobalRequestInterceptor
import foo.bar.clean.data.api.ktor.services.pollen.PollenApi
import foo.bar.clean.data.api.ktor.services.pollen.PollenServiceImp
import foo.bar.clean.data.api.ktor.services.temperature.TemperatureApi
import foo.bar.clean.data.api.ktor.services.temperature.TemperatureServiceImp
import foo.bar.clean.data.api.ktor.services.windspeed.WindSpeedApi
import foo.bar.clean.data.api.ktor.services.windspeed.WindSpeedServiceImp
import foo.bar.clean.domain.weather.PollenService
import foo.bar.clean.domain.weather.TemperatureService
import foo.bar.clean.domain.weather.WindSpeedService
import org.koin.dsl.module

@OptIn(ExperimentalStdlibApi::class)
val dataModule = module(override = true) {

    /**
     * Ktor
     */

    single {
        CustomKtorBuilder.create(
            GlobalRequestInterceptor(),
            InterceptorLogging()
        )//logging interceptor should be the last one
    }

    single {
        CallProcessorKtor(
            ErrorHandler(get())
        )
    }

    /**
     * Persistence
     */

    single {
        PerSista(
            dataDirectory = (get() as App).filesDir,
            logger = get()
        )
    }

    /**
     * Network Services
     */

    single<TemperatureService> {
        TemperatureServiceImp(
            client = TemperatureApi.create(get()),
            processor = get(),
            logger = get()
        )
    }

    single<WindSpeedService> {
        WindSpeedServiceImp(
            client = WindSpeedApi.create(get()),
            processor = get(),
            logger = get()
        )
    }

    single<PollenService> {
        PollenServiceImp(
            client = PollenApi.create(get()),
            processor = get(),
            logger = get()
        )
    }

    /**
     * Misc Data
     */

    single {
        SystemTimeWrapper()
    }

    single {
        ForeDelegateHolder.getLogger()
    }
}
