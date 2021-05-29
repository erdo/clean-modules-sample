package foo.bar.example.clean.domain.updater

import foo.bar.example.clean.domain.weather.WeatherModel

@ExperimentalStdlibApi
class UpdaterUseCase(
    private val weatherModel: WeatherModel,
) {
    fun execute() {
        weatherModel.fetchWeatherReport()
    }
}
