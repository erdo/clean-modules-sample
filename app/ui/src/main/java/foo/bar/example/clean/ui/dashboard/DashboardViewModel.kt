package foo.bar.example.clean.ui.dashboard

import co.early.fore.core.observer.Observable
import co.early.fore.kt.core.observer.ObservableImp
import foo.bar.example.clean.domain.ErrorResolution
import foo.bar.example.clean.domain.ErrorResolution.*
import foo.bar.example.clean.domain.refresher.UpdateModel
import foo.bar.example.clean.domain.weather.PollenLevel
import foo.bar.example.clean.domain.weather.WeatherModel
import foo.bar.example.clean.ui.R
import foo.bar.example.clean.ui.common.BaseViewModel

@ExperimentalStdlibApi
class DashboardViewModel (
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
                maxTempC = weatherModel.currentState.weatherReport.temperature.maxTempC,
                minTempC = weatherModel.currentState.weatherReport.temperature.minTempC,
                windSpeedKmpH = weatherModel.currentState.weatherReport.windSpeed.windSpeedKmpH,
                pollenLevelImageRes = weatherModel.currentState.weatherReport.pollenCount.pollenLevel.toImgRes(),
                isUpdating = weatherModel.currentState.isUpdating,
            ),
            UpdateViewState(
                timeElapsedPcent = updateModel.currentState.percentElapsedToNextUpdate(),
                autoRefreshing = !updateModel.currentState.updatesPaused
            ),
            userErrorMessage = weatherModel.currentState.error?.toUserMessage(),
            isBusy = weatherModel.currentState.isUpdating
        )

        notifyObservers()
    }

    fun updateNow(){
        weatherModel.fetchWeatherReport()
    }

    fun startUpdates(){
        updateModel.start()
    }

    fun stopUpdates(){
        updateModel.stop()
    }

    private fun ErrorResolution.toUserMessage() : String {
        return when (this) {
            CHECK_NETWORK_THEN_RETRY -> "please check network"
            RETRY_LATER -> "please try again"
            LOGIN_THEN_RETRY -> "please login"
        }
    }

    private fun PollenLevel.toImgRes() : Int {
        return when (this) {
            PollenLevel.HIGH -> R.drawable.pollen_high
            PollenLevel.MEDIUM -> R.drawable.pollen_medium
            PollenLevel.LOW -> R.drawable.pollen_low
            PollenLevel.UNKNOWN -> 0
        }
    }
}
