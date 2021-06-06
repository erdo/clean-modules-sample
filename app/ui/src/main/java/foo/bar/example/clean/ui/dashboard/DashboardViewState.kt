package foo.bar.example.clean.ui.dashboard

data class DashboardViewState(
    val weather: WeatherViewState = WeatherViewState(),
    val updateViewState: UpdateViewState = UpdateViewState(),
    val userErrorMessage: String? = null,
    val isBusy: Boolean = false,
)

data class WeatherViewState(
    val maxTempC: Int = 0,
    val minTempC: Int = 0,
    val windSpeedKmpH: Int = 0,
    val pollenLevelImageRes: Int = 0,
    val isUpdating: Boolean = false,
)

data class UpdateViewState(
    val timeElapsedPcent: Float = 0F,
    val autoRefreshing: Boolean = false,
)
