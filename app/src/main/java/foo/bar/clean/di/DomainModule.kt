package foo.bar.clean.di

import foo.bar.clean.domain.refresher.RefreshModel
import foo.bar.clean.domain.weather.WeatherAction
import foo.bar.clean.domain.weather.WeatherModel
import org.koin.dsl.module

val domainModule = module {

    /**
     * Models
     */

    single {
        RefreshModel(
            refresh = { (get() as WeatherModel).send(WeatherAction.FetchWeatherReport) },
            refreshIntervalSeconds = 10,
            systemTimeWrapper = get(),
            logger = get()
        )
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
