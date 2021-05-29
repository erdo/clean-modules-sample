package foo.bar.example.clean.ui.dashboard

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import co.early.fore.core.ui.SyncableView
import co.early.fore.kt.core.delegate.ForeDelegateHolder
import co.early.fore.kt.core.ui.ForeLifecycleObserver
import co.early.fore.kt.core.ui.SyncTrigger
import co.early.fore.kt.core.ui.showOrGone
import co.early.fore.kt.core.ui.showOrInvisible
import coil.load
import foo.bar.example.clean.domain.ErrorResolution.*
import foo.bar.example.clean.domain.updater.UpdateModel
import foo.bar.example.clean.domain.weather.WeatherModel
import foo.bar.example.clean.ui.R
import foo.bar.example.clean.ui.common.showToast
import kotlinx.android.synthetic.main.activity_dashboard.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalStdlibApi
class DashboardActivity : FragmentActivity(R.layout.activity_dashboard), SyncableView {

    //models that we need to sync with
    private val viewModel: DashboardViewModel by viewModel()

    private lateinit var showErrorSyncTrigger: SyncTrigger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setup observers
        lifecycle.addObserver(ForeLifecycleObserver(this, viewModel))

        //set up click listeners
        dashboard_startupdates_btn.setOnClickListener { viewModel.startUpdates() }
        dashboard_stopupdates_btn.setOnClickListener { viewModel.stopUpdates() }
        dashboard_updatenow_btn.setOnClickListener { viewModel.updateNow() }

        //set up syncTriggers
        showErrorSyncTrigger = SyncTrigger(
            triggeredWhen = { viewModel.viewState.userErrorMessage != null }
        ){
            viewModel.viewState.userErrorMessage?.let{ msg ->
                showToast(msg)
            }
        }
    }

    override fun syncView() {

        viewModel.viewState.apply {
            dashboard_busy_progbar.showOrInvisible(isBusy)
            dashboard_updating_text.showOrInvisible(!updateViewState.autoRefreshing)
            dashboard_updating_percentbar.showOrInvisible(updateViewState.autoRefreshing)
            dashboard_updating_percentbar.setPercent(updateViewState.timeElapsedPcent)
        }

//        viewModel.state.weather.apply {
//            weather_busy_prog.showOrInvisible(isUpdating)
//            weather_container_view.showOrInvisible(!isUpdating)
//            weather_max_txt.text = maxTempC
//            weather_min_txt.text = minTempC
//            weather_desc_txt.text = weatherDesc
//            weather_icon_img.src = weatherIconRes
//        }
//
//        launch_fetch_btn.isEnabled = !launchRepo.currentState.isUpdating
//        launch_id_textview.text = "id: ${launchRepo.currentState.launch.id}"
//        launch_patch_img.load(launchRepo.currentState.launch.patchImgUrl)
//        launch_busy_progbar.showOrInvisible(launchRepo.currentState.isUpdating)
//        launch_detailcontainer_linearlayout.showOrGone(!launchRepo.currentState.isUpdating)

        showErrorSyncTrigger.checkLazy()
    }
}
