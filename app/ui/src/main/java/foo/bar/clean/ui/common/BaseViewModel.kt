package foo.bar.clean.ui.common

import androidx.lifecycle.ViewModel
import co.early.fore.core.observer.Observable
import foo.bar.clean.domain.common.*

@Suppress("LeakingThis")
abstract class BaseViewModel<VS: State, VA: Action>(
    initialState: VS,
    vararg udfModels: UdfModel<*, *>
) : ViewModel(), StateUpdater<VS>, UdfModel<VS, VA>,
    StateViewModel<VS> by StateViewModelImp(
        initialState, *udfModels
    ) {

    init {
        initViewModel(this)
    }

    abstract override fun getUpdatedViewState(): VS
}
