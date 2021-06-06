package foo.bar.example.clean.domain.mediators

import foo.bar.example.clean.domain.weather.WeatherModel

@ExperimentalStdlibApi
class OnRefreshMediator(
    private val weatherModel: WeatherModel,
) {
    fun refreshNow() {
        weatherModel.fetchWeatherReport()
    }
}
