package foo.bar.clean.domain.mediators

import foo.bar.clean.domain.weather.WeatherModel

@ExperimentalStdlibApi
class OnRefreshMediator(
    private val weatherModel: WeatherModel,
) {
    fun refreshNow() {
        weatherModel.fetchWeatherReport()
    }
}
