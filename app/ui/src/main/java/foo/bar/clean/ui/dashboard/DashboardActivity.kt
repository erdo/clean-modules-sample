package foo.bar.clean.ui.dashboard

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import co.early.fore.core.ui.SyncableView
import co.early.fore.kt.core.ui.LifecycleObserver
import co.early.fore.kt.core.ui.showOrInvisible
import co.early.fore.kt.core.ui.trigger.ResetRule
import co.early.fore.kt.core.ui.trigger.TriggerOnChange
import co.early.fore.kt.core.ui.trigger.TriggerWhen
import foo.bar.clean.domain.weather.PollenLevel
import foo.bar.clean.ui.R
import foo.bar.clean.ui.common.prettyPrint
import foo.bar.clean.ui.common.showToast
import foo.bar.clean.ui.common.toImgRes
import kotlinx.android.synthetic.main.inc_dashboard.*
import kotlinx.android.synthetic.main.inc_diagnostics.*
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalStdlibApi
class DashboardActivity : FragmentActivity(R.layout.activity_dashboard), SyncableView {

    //models that we need to sync with
    private val viewModel: DashboardViewModel by viewModel()

    private lateinit var showErrorTrigger: TriggerWhen
    private lateinit var fadePollenTrigger: TriggerOnChange<PollenLevel>
    private lateinit var rotateWindTurbineTrigger: TriggerOnChange<Int>
    private lateinit var animations: DashboardAnimations

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setup observers
        lifecycle.addObserver(LifecycleObserver(this, viewModel))

        //set up click listeners
        dashboard_startautorefresh_btn.setOnClickListener { viewModel.startAutoRefresh() }
        dashboard_stopautorefresh_btn.setOnClickListener { viewModel.stopAutoRefresh() }
        dashboard_updatenow_btn.setOnClickListener { viewModel.updateNow() }

        // set up animations
        animations = DashboardAnimations(
            dashboard_windturbine_img,
            dashboard_pollenlevel_img,
            dashboard_pollenbackground_img,
        )

        setupTriggers()
    }

    override fun syncView() {

        viewModel.viewState.apply {
            dashboard_busy.showOrInvisible(isUpdating)
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

        // this lets us fire one off events from changes of state, you can use your
        // preferred method, see here for more details:
        // https://erdo.github.io/android-fore/01-views.html#synctrigger
        showErrorTrigger.checkLazy()
        fadePollenTrigger.checkLazy()
        rotateWindTurbineTrigger.check()
    }

    /**
     * This is how we bridge the gap between a state based architecture to an event based UI trigger
     * see here for more details: https://erdo.github.io/android-fore/01-views.html#triggers
     */
    private fun setupTriggers() {

        showErrorTrigger = TriggerWhen(
            triggeredWhen = { viewModel.viewState.error != null },
            doThisWhenTriggered = { showToast(viewModel.viewState.error) }
        ).resetRule(ResetRule.ONLY_AFTER_REVERSION)

        fadePollenTrigger = TriggerOnChange(
            currentState = { viewModel.viewState.weather.pollenLevel },
            doThisWhenTriggered = { animations.animatePollenChange() }
        )

        rotateWindTurbineTrigger = TriggerOnChange({ viewModel.viewState.weather.windSpeedKmpH }) {
            animations.animateWindSpeedChange(viewModel.viewState.weather.windSpeedPercent())
        }
    }
}
