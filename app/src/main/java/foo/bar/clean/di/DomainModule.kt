package foo.bar.clean.di

import foo.bar.clean.domain.refresher.RefreshModel
import foo.bar.clean.domain.mediators.OnRefreshMediator
import foo.bar.clean.domain.weather.WeatherModel
import org.koin.dsl.module

@ExperimentalStdlibApi
val domainModule = module(override = true) {

    /**
     * Models and Mediators
     */

    single {
        RefreshModel(
            onRefreshMediator = get(),
            refreshIntervalMilliSeconds = 10000,
            dispatcher = get(),
            systemTimeWrapper = get(),
            logger = get()
        )
    }

    single {
        OnRefreshMediator(get())
    }

    single {
        WeatherModel(
            pollenService = get(),
            temperatureService = get(),
            windSpeedService = get(),
            dispatcher = get(),
            perSista = get(),
            logger = get()
        )
    }
}
