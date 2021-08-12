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
            get(),
            10000,
            get(),
            get(),
            get(),
            get()
        )
    }

    single {
        OnRefreshMediator(get())
    }

    single {
        WeatherModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
}
