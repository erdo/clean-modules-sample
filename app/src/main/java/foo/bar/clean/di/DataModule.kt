package foo.bar.clean.di

import co.early.fore.core.time.SystemTimeWrapper
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.kt.net.InterceptorLogging
import co.early.fore.kt.net.ktor.CallWrapperKtor
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

val dataModule = module {

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
        CallWrapperKtor(
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
            wrapper = get(),
            logger = get()
        )
    }

    single<WindSpeedService> {
        WindSpeedServiceImp(
            client = WindSpeedApi.create(get()),
            wrapper = get(),
            logger = get()
        )
    }

    single<PollenService> {
        PollenServiceImp(
            client = PollenApi.create(get()),
            wrapper = get(),
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
        Fore.getLogger()
    }
}
