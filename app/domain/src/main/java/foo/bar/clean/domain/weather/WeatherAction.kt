package foo.bar.clean.domain.weather

import foo.bar.clean.domain.common.Action

sealed class WeatherAction : Action {
    object FetchWeatherReport : WeatherAction()
}