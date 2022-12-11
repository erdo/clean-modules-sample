package foo.bar.clean.domain.common

import co.early.fore.core.observer.Observable

interface Action
interface State

interface StateModelReadable<S: State> {
    fun currentState(): S
}

interface StateModelWritable<S: State> {
    fun updateState(update: S.() -> S)
}

interface ActionReceiver<A: Action> {
    fun send(action: A)
}

interface StateUpdater<S: State> {
    fun getUpdatedViewState(): S
}

interface StateViewModel<S: State>: StateModelReadable<S>, Observable {
    fun initViewModel(viewModel: StateUpdater<S>)
}

interface StateModel<S: State>: StateModelReadable<S>, StateModelWritable<S>, Observable
interface UdfModel<S: State, A: Action>: StateModelReadable<S>, ActionReceiver<A>, Observable

