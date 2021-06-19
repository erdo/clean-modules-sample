package foo.bar.example.clean.ui.dashboard

import co.early.fore.core.observer.Observable
import co.early.fore.kt.core.observer.ObservableImp
import foo.bar.example.clean.domain.refresher.UpdateModel
import foo.bar.example.clean.domain.weather.WeatherModel
import foo.bar.example.clean.ui.common.BaseViewModel
import foo.bar.example.clean.ui.common.toImgRes

@ExperimentalStdlibApi
class DashboardViewModel(
    private val weatherModel: WeatherModel,
    private val updateModel: UpdateModel,
) : BaseViewModel(
    weatherModel,
    updateModel,
), Observable by ObservableImp() {

    var viewState = DashboardViewState()
        private set

    init {
        syncView()
    }

    override fun syncView() {

        viewState = DashboardViewState(
            WeatherViewState(
                maxTempC = weatherModel.currentState.weatherReport.temperature.maxTempC
                    ?: MIN_DIAL_TEMP,
                minTempC = weatherModel.currentState.weatherReport.temperature.minTempC
                    ?: MIN_DIAL_TEMP,
                windSpeedKmpH = weatherModel.currentState.weatherReport.windSpeed.windSpeedKmpH,
                pollenLevel = weatherModel.currentState.weatherReport.pollenCount.pollenLevel,
            ),
            AutoRefreshViewState(
                timeElapsedPcent = updateModel.currentState.percentElapsedToNextUpdate(),
                autoRefreshing = !updateModel.currentState.updatesPaused
            ),
            errorResolution = weatherModel.currentState.error,
            isUpdating = weatherModel.currentState.isUpdating
        )

        notifyObservers()
    }

    fun updateNow() {
        weatherModel.fetchWeatherReport()
    }

    fun startUpdates() {
        updateModel.start()
    }

    fun stopUpdates() {
        updateModel.stop()
    }
}
