package foo.bar.clean.di

import foo.bar.clean.App
import foo.bar.clean.ui.dashboard.DashboardViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@ExperimentalStdlibApi
val uiModule = module(override = true) {

    /**
     * Misc
     */

    single { androidContext() as App }

    /**
     * ViewModel
     */

    viewModel {
        DashboardViewModel(
            weatherModel = get(),
            refreshModel = get()
        )
    }
}
