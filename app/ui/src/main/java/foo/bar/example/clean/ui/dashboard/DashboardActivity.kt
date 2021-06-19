package foo.bar.example.clean.ui.dashboard

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import co.early.fore.core.ui.SyncableView
import co.early.fore.kt.core.ui.ForeLifecycleObserver
import co.early.fore.kt.core.ui.showOrInvisible
import co.early.fore.kt.core.ui.synctrigger.ResetRule
import co.early.fore.kt.core.ui.synctrigger.SyncTrigger
import co.early.fore.kt.core.ui.synctrigger.SyncTriggerKeeper
import foo.bar.example.clean.domain.weather.PollenLevel
import foo.bar.example.clean.ui.R
import foo.bar.example.clean.ui.common.prettyPrint
import foo.bar.example.clean.ui.common.showToast
import foo.bar.example.clean.ui.common.toImgRes
import kotlinx.android.synthetic.main.inc_dashboard.*
import kotlinx.android.synthetic.main.inc_diagnostics.*
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalStdlibApi
class DashboardActivity : FragmentActivity(R.layout.activity_dashboard), SyncableView {

    //models that we need to sync with
    private val viewModel: DashboardViewModel by viewModel()

    private lateinit var showErrorSyncTrigger: SyncTrigger
    private lateinit var fadePollenSyncTrigger: SyncTriggerKeeper<PollenLevel>
    private lateinit var rotateWindTurbineSyncTrigger: SyncTriggerKeeper<Int>
    private lateinit var animations: DashboardAnimations

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setup observers
        lifecycle.addObserver(ForeLifecycleObserver(this, viewModel))

        //set up click listeners
        dashboard_startautorefresh_btn.setOnClickListener { viewModel.startUpdates() }
        dashboard_stopautorefresh_btn.setOnClickListener { viewModel.stopUpdates() }
        dashboard_updatenow_btn.setOnClickListener { viewModel.updateNow() }

        // set up animations
        animations = DashboardAnimations(
            dashboard_windturbine_img,
            dashboard_pollenlevel_img,
            dashboard_pollenbackground_img,
            this
        )

        setupSyncTriggers()
    }

    override fun syncView() {

        viewModel.viewState.apply {
            dashboard_busy_progbar.showOrInvisible(isUpdating)
            dashboard_updating_text.showOrInvisible(!autoRefresh.autoRefreshing)
            dashboard_startautorefresh_btn.isEnabled = (!autoRefresh.autoRefreshing)
            dashboard_stopautorefresh_btn.isEnabled = (autoRefresh.autoRefreshing)
            dashboard_updatenow_btn.isEnabled = (!isUpdating)
            dashboard_update_countdown.setPercent(autoRefresh.timeElapsedPcent)
            dashboard_pollenlevel_img.setImageResource(weather.pollenLevel.toImgRes())
            dashboard_tempmaxmin.setMaxPercent(weather.maxTempPercent())
            dashboard_tempmaxmin.setMinPercent(weather.minTempPercent())
            diagnostics_viewstate.text = this.prettyPrint()
        }

        showErrorSyncTrigger.checkLazy()
        fadePollenSyncTrigger.checkLazy()
        rotateWindTurbineSyncTrigger.check()
    }

    /**
     * This is how we bridge the gap between a state based architecture to an event based UI trigger
     * see here for more details: https://erdo.github.io/android-fore/01-views.html#synctrigger
     */
    private fun setupSyncTriggers() {

        showErrorSyncTrigger = SyncTrigger(
            triggeredWhen = { viewModel.viewState.errorResolution != null },
            doThisWhenTriggered = {
                viewModel.viewState.errorResolution?.let { msg ->
                    showToast(msg)
                }
            }
        )

        fadePollenSyncTrigger = SyncTriggerKeeper<PollenLevel>(
            triggeredWhen = { keeper ->
                keeper.swap { viewModel.viewState.weather.pollenLevel }
            },
            doThisWhenTriggered = {
                animations.animatePollenChange()
            }
        ).resetRule(ResetRule.IMMEDIATELY)

        rotateWindTurbineSyncTrigger = SyncTriggerKeeper<Int>(
            triggeredWhen = { keeper ->
                keeper.swap { viewModel.viewState.weather.windSpeedKmpH }
            },
            doThisWhenTriggered = {
                animations.animateWindSpeedChange(viewModel.viewState.weather.windSpeedPercent())
            }
        ).resetRule(ResetRule.IMMEDIATELY)
    }
}
