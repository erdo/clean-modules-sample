package foo.bar.example.clean.di

import foo.bar.example.clean.domain.refresher.UpdateModel
import foo.bar.example.clean.domain.mediators.OnRefreshMediator
import foo.bar.example.clean.domain.weather.WeatherModel
import org.koin.dsl.module

@ExperimentalStdlibApi
val domainModule = module(override = true) {

    /**
     * Models and Mediators
     */

    single {
        UpdateModel(
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
