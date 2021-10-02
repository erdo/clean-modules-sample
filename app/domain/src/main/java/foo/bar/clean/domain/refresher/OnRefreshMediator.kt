package foo.bar.clean.domain.refresher

import foo.bar.clean.domain.weather.WeatherModel


class OnRefreshMediator(
    private val weatherModel: WeatherModel,
) {
    fun refreshNow() {
        weatherModel.fetchWeatherReport()
    }
}
