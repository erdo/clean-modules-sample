package foo.bar.clean.ui.dashboard

import co.early.fore.kt.core.delegate.Fore
import foo.bar.clean.domain.common.UdfModel
import foo.bar.clean.domain.refresher.RefreshAction
import foo.bar.clean.domain.refresher.RefreshState
import foo.bar.clean.domain.weather.WeatherAction
import foo.bar.clean.domain.weather.WeatherState
import foo.bar.clean.ui.common.BaseViewModel
import foo.bar.clean.ui.dashboard.DashboardViewAction.*

class DashboardViewModel(
    private val weatherModel: UdfModel<WeatherState, WeatherAction>,
    private val refreshModel: UdfModel<RefreshState, RefreshAction>,
) : BaseViewModel<DashboardViewState, DashboardViewAction>(
    DashboardViewState(), weatherModel, refreshModel
) {

    // this gets called whenever our domain models' state changes
    override fun getUpdatedViewState(): DashboardViewState {
        return DashboardViewState(
            WeatherViewState(
                maxTempC = weatherModel.currentState().weatherReport.temperature.maxTempC
                    ?: MIN_DIAL_TEMP,
                minTempC = weatherModel.currentState().weatherReport.temperature.minTempC
                    ?: MIN_DIAL_TEMP,
                windSpeedKmpH = weatherModel.currentState().weatherReport.windSpeed.windSpeedKmpH,
                pollenLevel = weatherModel.currentState().weatherReport.pollenCount.pollenLevel,
            ),
            AutoRefreshViewState(
                timeElapsedPcent = refreshModel.currentState().percentElapsedToNextRefresh(),
                autoRefreshing = !refreshModel.currentState().paused
            ),
            error = weatherModel.currentState().error,
            isUpdating = weatherModel.currentState().isUpdating
        )
    }

    override fun send(action: DashboardViewAction) {
        when (action){
            FetchWeatherReport -> weatherModel.send(WeatherAction.FetchWeatherReport)
            StartAutoRefresh -> refreshModel.send(RefreshAction.Start)
            StopAutoRefresh -> refreshModel.send(RefreshAction.Stop)
        }
    }
}
