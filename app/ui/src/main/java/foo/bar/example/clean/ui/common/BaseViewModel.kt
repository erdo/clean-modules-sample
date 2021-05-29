package foo.bar.example.clean.ui.common

import androidx.lifecycle.ViewModel
import co.early.fore.core.observer.Observable
import co.early.fore.core.observer.ObservableGroup
import co.early.fore.kt.core.observer.ObservableGroupImp
import co.early.fore.core.observer.Observer
import co.early.fore.core.ui.SyncableView

/**
 * A ViewModel that observes several observable models, takes care of lifecycle / memory
 * concerns
 *
 * syncView() gets called each time any of the models change their state
 */
@Suppress("LeakingThis")
abstract class BaseViewModel(vararg observablesList: Observable): ViewModel(), SyncableView {

    private val observableGroup: ObservableGroup
    private val observer = Observer { syncView() }

    init {
        observableGroup = ObservableGroupImp(*observablesList)
        observableGroup.addObserver(observer)
    }

    override fun onCleared() {
        super.onCleared()
        observableGroup.removeObserver(observer)
    }
}
