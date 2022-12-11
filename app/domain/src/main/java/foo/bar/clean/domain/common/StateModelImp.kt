package foo.bar.clean.domain.common

import co.early.fore.core.observer.Observable
import co.early.fore.kt.core.observer.ObservableImp

class StateModelImp<S : State>(
    initialState: S
) : StateModel<S>, Observable by ObservableImp() {

    private var st8: S

    init {
        st8 = initialState
    }

    override fun currentState(): S = st8

    override fun updateState(update: S.() -> S) {
        st8 = st8.update()
        notifyObservers()
    }
}
