package foo.bar.clean.domain.common

import co.early.fore.core.observer.Observable
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.kt.core.observer.ObservableImp
import co.early.persista.PerSista

class PersistentStateModelImp<S : State>(
    initialState: S,
    private var stateType: kotlin.reflect.KType,
    private val perSista: PerSista,
) : StateModel<S>, Observable by ObservableImp() {

    private var st8: S

    init {
        st8 = initialState
        perSista.read(st8, stateType) { readState ->
            st8 = readState
            notifyObservers()
        }
    }

    override fun currentState(): S = st8

    override fun updateState(update: S.() -> S) {
        perSista.write(st8.update(), stateType) { saved ->
            st8 = saved
            notifyObservers()
        }
    }
}
