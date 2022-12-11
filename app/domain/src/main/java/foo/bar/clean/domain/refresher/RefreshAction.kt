package foo.bar.clean.domain.refresher

import foo.bar.clean.domain.common.Action

sealed class RefreshAction: Action {
    object Start : RefreshAction()
    object Stop : RefreshAction()
}