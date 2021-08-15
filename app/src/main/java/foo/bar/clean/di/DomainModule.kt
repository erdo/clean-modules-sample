package foo.bar.clean.di

import foo.bar.clean.domain.refresher.RefreshModel
import foo.bar.clean.domain.refresher.OnRefreshMediator
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
            perSista = get(),
            logger = get()
        )
    }
}
