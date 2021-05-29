package foo.bar.example.clean.di

import foo.bar.example.clean.domain.updater.UpdateModel
import foo.bar.example.clean.domain.updater.UpdaterUseCase
import foo.bar.example.clean.domain.weather.WeatherModel
import foo.bar.example.clean.ui.dashboard.DashboardViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@ExperimentalStdlibApi
val domainModule = module(override = true) {

    /**
     * Models, States and UseCases
     */

    single {
        UpdateModel(
            get(),
            20000,
            get(),
            get(),
            get(),
            get()
        )
    }

    single {
        UpdaterUseCase(get())
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
