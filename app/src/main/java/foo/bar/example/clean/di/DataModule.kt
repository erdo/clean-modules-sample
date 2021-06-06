package foo.bar.example.clean.di

import co.early.fore.core.time.SystemTimeWrapper
import co.early.fore.kt.core.delegate.ForeDelegateHolder
import co.early.fore.kt.net.InterceptorLogging
import co.early.fore.kt.net.ktor.CallProcessorKtor
import co.early.persista.PerSista
import foo.bar.example.clean.App
import foo.bar.example.clean.data.api.ktor.ErrorHandler
import foo.bar.example.clean.data.api.ktor.GlobalRequestInterceptor
import foo.bar.example.clean.data.api.ktor.services.pollen.PollenApi
import foo.bar.example.clean.data.api.ktor.services.pollen.PollenServiceImp
import foo.bar.example.clean.data.api.ktor.services.temperature.TemperatureApi
import foo.bar.example.clean.data.api.ktor.services.temperature.TemperatureServiceImp
import foo.bar.example.clean.data.api.ktor.services.windspeed.WindSpeedApi
import foo.bar.example.clean.data.api.ktor.services.windspeed.WindSpeedServiceImp
import foo.bar.example.clean.domain.weather.PollenService
import foo.bar.example.clean.domain.weather.TemperatureService
import foo.bar.example.clean.domain.weather.WindSpeedService
import foo.bar.example.forektorkt.api.CustomKtorBuilder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

@ExperimentalStdlibApi
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
            logger = ForeDelegateHolder.getLogger()
        )
    }

    /**
     * Network Services
     */

    single<TemperatureService> {
        TemperatureServiceImp(
            TemperatureApi.create(get()),
            get(),
            get()
        )
    }

    single<WindSpeedService> {
        WindSpeedServiceImp(
            WindSpeedApi.create(get()),
            get(),
            get()
        )
    }

    single<PollenService> {
        PollenServiceImp(
            PollenApi.create(get()),
            get(),
            get()
        )
    }

    /**
     * Misc Data
     */

    single {
        SystemTimeWrapper()
    }

    single<CoroutineDispatcher> {
        Dispatchers.Main.immediate
    }

    single {
        ForeDelegateHolder.getLogger()
    }
}
