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
import foo.bar.clean.ui.common.prettyPrint
import foo.bar.clean.ui.common.showToast
import foo.bar.clean.ui.common.toImgRes
import foo.bar.clean.ui.databinding.ActivityDashboardBinding
import foo.bar.clean.ui.databinding.IncDashboardBinding
import foo.bar.clean.ui.databinding.IncDiagnosticsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalStdlibApi
class DashboardActivity : FragmentActivity(), SyncableView {

    //models that we need to sync with
    private val viewModel: DashboardViewModel by viewModel()

    private lateinit var showErrorTrigger: TriggerWhen
    private lateinit var fadePollenTrigger: TriggerOnChange<PollenLevel>
    private lateinit var rotateWindTurbineTrigger: TriggerOnChange<Int>
    private var animations: DashboardAnimations? = null

    private lateinit var dashboardVb: IncDashboardBinding
    private lateinit var diagnosticsVb: IncDiagnosticsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setup viewBinding
        val activityDashboardBinding = ActivityDashboardBinding.inflate(layoutInflater)
        dashboardVb = activityDashboardBinding.dashboardContainer
        diagnosticsVb = activityDashboardBinding.diagnosticContainer
        setContentView(activityDashboardBinding.root)

        // setup observers
        lifecycle.addObserver(LifecycleObserver(this, viewModel))

        // set up click listeners
        dashboardVb.apply {
            startautorefreshBtn.setOnClickListener { viewModel.startAutoRefresh() }
            stopautorefreshBtn.setOnClickListener { viewModel.stopAutoRefresh() }
            updatenowBtn.setOnClickListener { viewModel.updateNow() }
        }

        // set up animations
        dashboardVb.apply {
            animations = DashboardAnimations(
                windturbineImg,
                pollenlevelImg,
                pollenbackgroundImg,
            )
        }

        setupTriggers()
    }

    override fun syncView() {

        viewModel.viewState.apply {
            // main UI panel
            dashboardVb.busy.showOrInvisible(isUpdating)
            dashboardVb.updatingText.showOrInvisible(!autoRefresh.autoRefreshing)
            dashboardVb.startautorefreshBtn.isEnabled = (!autoRefresh.autoRefreshing)
            dashboardVb.stopautorefreshBtn.isEnabled = (autoRefresh.autoRefreshing)
            dashboardVb.updatenowBtn.isEnabled = (!isUpdating)
            dashboardVb.updateCountdown.setPercent(autoRefresh.timeElapsedPcent)
            dashboardVb.pollenlevelImg.setImageResource(weather.pollenLevel.toImgRes())
            dashboardVb.tempmaxmin.setMaxPercent(weather.maxTempPercent())
            dashboardVb.tempmaxmin.setMinPercent(weather.minTempPercent())
            // diagnostics panel
            diagnosticsVb.viewText.text = prettyPrint()
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
            doThisWhenTriggered = { animations?.animatePollenChange() }
        )

        rotateWindTurbineTrigger = TriggerOnChange({ viewModel.viewState.weather.windSpeedKmpH }) {
            animations?.animateWindSpeedChange(viewModel.viewState.weather.windSpeedPercent())
        }
    }
}
