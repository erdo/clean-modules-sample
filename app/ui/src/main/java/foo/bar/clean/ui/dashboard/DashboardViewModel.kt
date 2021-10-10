package foo.bar.clean.ui.dashboard

import androidx.lifecycle.ViewModel
import co.early.fore.core.ui.SyncableView
import co.early.fore.kt.core.ui.ViewModelObservability
import co.early.fore.kt.core.ui.ViewModelObservabilityImp
import foo.bar.clean.domain.refresher.RefreshModel
import foo.bar.clean.domain.weather.WeatherModel

class DashboardViewModel(
    private val weatherModel: WeatherModel,
    private val refreshModel: RefreshModel,
) : ViewModel(), SyncableView, ViewModelObservability by ViewModelObservabilityImp(
    weatherModel, refreshModel
) {

    var viewState = DashboardViewState()
        private set

    init {
        initSyncableView(this)
    }

    // this gets called whenever our domain models' state changes
    override fun syncView() {

        viewState = DashboardViewState(
            WeatherViewState(
                maxTempC = weatherModel.currentState.weatherReport.temperature.maxTempC ?: MIN_DIAL_TEMP,
                minTempC = weatherModel.currentState.weatherReport.temperature.minTempC ?: MIN_DIAL_TEMP,
                windSpeedKmpH = weatherModel.currentState.weatherReport.windSpeed.windSpeedKmpH,
                pollenLevel = weatherModel.currentState.weatherReport.pollenCount.pollenLevel,
            ),
            AutoRefreshViewState(
                timeElapsedPcent = refreshModel.currentState.percentElapsedToNextUpdate(),
                autoRefreshing = !refreshModel.currentState.updatesPaused
            ),
            error = weatherModel.currentState.error,
            isUpdating = weatherModel.currentState.isUpdating
        )

        notifyObservers()
    }

    fun updateNow() {
        // these are fire and forget, the observers take care of any resulting changes
        weatherModel.fetchWeatherReport()
    }

    fun startAutoRefresh() {
        // these are fire and forget, the observers take care of any resulting changes
        refreshModel.start()
    }

    fun stopAutoRefresh() {
        // these are fire and forget, the observers take care of any resulting changes
        refreshModel.stop()
    }
}
