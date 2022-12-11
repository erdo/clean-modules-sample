package foo.bar.clean.ui.dashboard

import foo.bar.clean.domain.common.Action

sealed class DashboardViewAction : Action {
    object FetchWeatherReport : DashboardViewAction()
    object StartAutoRefresh : DashboardViewAction()
    object StopAutoRefresh : DashboardViewAction()
}