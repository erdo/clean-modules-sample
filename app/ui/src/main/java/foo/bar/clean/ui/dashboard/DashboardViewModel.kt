package foo.bar.clean.ui.dashboard

import co.early.fore.core.observer.Observable
import co.early.fore.kt.core.observer.ObservableImp
import foo.bar.clean.domain.refresher.RefreshModel
import foo.bar.clean.domain.weather.WeatherModel
import foo.bar.clean.ui.common.BaseViewModel

class DashboardViewModel(
    private val weatherModel: WeatherModel,
    private val refreshModel: RefreshModel,
) : BaseViewModel(
    weatherModel,
    refreshModel,
), Observable by ObservableImp() {

    var viewState = DashboardViewState()
        private set

    init {
        syncView()
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
