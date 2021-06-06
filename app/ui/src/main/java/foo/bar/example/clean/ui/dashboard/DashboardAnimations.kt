//package foo.bar.example.clean.ui.dashboard
//
//import android.animation.Animator
//import android.animation.AnimatorListenerAdapter
//import android.animation.AnimatorSet
//import android.animation.ObjectAnimator
//import android.graphics.Rect
//import android.os.Handler
//import android.view.View
//import android.view.View.INVISIBLE
//import android.view.View.VISIBLE
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import co.early.asaf.core.callbacks.SuccessCallBack
//import co.early.asaf.core.ui.SyncableView
//import co.early.asaf.ui.SyncTrigger
//import co.early.password123.feature.networkmonitor.NetworkState
//import co.early.password123.ui.common.ViewUtils
//import co.early.password123.ui.common.anim.CustomEasing
//import co.early.password123.ui.common.anim.SyncerAnimationComplete
//import co.early.password123.ui.common.widgets.LockedKeyboardEditText
//import co.early.pwned.Pwned
//import co.early.pwned.feature.PwnedResult.IsPwned.UNKNOWN
//
//
//class DashboardAnimations(netWarnIcon: View,
//                          netWarnBgd: View,
//                          infoIcon: View,
//                          busyIcon: View,
//                          initProgress: View,
//                          warningText: TextView,
//                          warningDetail: TextView,
//                          cloudIcon: ImageView,
//                          syncableView: SyncableView,
//                          networkState: NetworkState,
//                          pwned: Pwned,
//                          private val screenWidthPx: Float,
//                          private val screenHeightPx: Float,
//                          private val densityScalingFactor: Float) {
//
//    //triggered automatically
//    private val networkGoneAnimSet = AnimatorSet()
//    private val networkBackAnimSet = AnimatorSet()
//    private val showBusyAnimSet = AnimatorSet()
//    private val hideBusyAnimSet = AnimatorSet()
//    private val showInfoAnimSet = AnimatorSet()
//    private val hideInfoAnimSet = AnimatorSet()
//    private val removeLoadingBarAnimSet = AnimatorSet()
//    private val jiggleAnimSet = AnimatorSet()
//
//    private val networkGoneTrigger: SyncTrigger
//    private val networkBackTrigger: SyncTrigger
//    private val showBusyTrigger: SyncTrigger
//    private val hideBusyTrigger: SyncTrigger
//    private val showInfoTrigger: SyncTrigger
//    private val hideInfoTrigger: SyncTrigger
//    private val removeLoadingBarTrigger: SyncTrigger
//    private val jiggleTrigger: SyncTrigger
//
//    private val jiggleAngle1 = 50f
//    private val jiggleAngle2 = 20f
//    private val searchingTransitionAnimDuration: Long = 400
//    private val networkGoneAnimDuration: Long = 700
//    private val networkBackAnimDuration: Long = 400
//    private val clearScreenDuration: Long = 400
//
//
//    init {
//
//        networkGoneTrigger = networkGoneTrigger(networkGoneAnimSet, netWarnIcon, netWarnBgd, syncableView, networkState)
//        networkBackTrigger = networkBackTrigger(networkBackAnimSet, netWarnIcon, netWarnBgd, syncableView, networkState)
//        showBusyTrigger = showBusyTrigger(showBusyAnimSet, busyIcon, syncableView, pwned)
//        hideBusyTrigger = hideBusyTrigger(hideBusyAnimSet, busyIcon, syncableView, pwned)
//        showInfoTrigger = showInfoIconTrigger(showInfoAnimSet, infoIcon, syncableView, pwned)
//        hideInfoTrigger = hideInfoIconTrigger(hideInfoAnimSet, infoIcon, syncableView, pwned)
//        removeLoadingBarTrigger = setupRemoveLoadingBarTrigger(removeLoadingBarAnimSet, initProgress, syncableView, pwned)
//        jiggleTrigger = jiggleTrigger(jiggleAnimSet, infoIcon, netWarnIcon, syncableView)
//
//        ViewUtils.allowAnimationOutsideParent(infoIcon)
//        ViewUtils.allowAnimationOutsideParent(warningText)
//        ViewUtils.allowAnimationOutsideParent(warningDetail)
//        ViewUtils.allowAnimationOutsideParent(cloudIcon)
//    }
//
//    private fun networkGoneTrigger(
//            animatorSet: AnimatorSet,
//            netWarnIcon: View,
//            backgroundWarn: View,
//            syncableView: SyncableView,
//            networkState: NetworkState): SyncTrigger {
//
//        animatorSet.duration = networkGoneAnimDuration
//
//        val bounceDownObjAnimator = ObjectAnimator.ofFloat(netWarnIcon, "translationY", -screenHeightPx / 2f, 0f)
//        bounceDownObjAnimator.setInterpolator(CustomEasing.bounceDown)
//        val backgroundFadeObjAnimator = ObjectAnimator.ofFloat(backgroundWarn, "alpha", 0f, 1f)
//
//        animatorSet.playTogether(
//                bounceDownObjAnimator,
//                backgroundFadeObjAnimator)
//        animatorSet.addListener(SyncerAnimationComplete(syncableView, backgroundWarn, netWarnIcon))//onAnimationEnd() -> resetAlpha values, then call syncView()
//
//        return SyncTrigger({
//            // temporarily adjust visibility before running animation, syncView() gets
//            // called at the end of the animation anyway to put everything back to how it should be
//            netWarnIcon.visibility = VISIBLE
//            backgroundWarn.visibility = VISIBLE
//            animatorSet.start()
//        }) { !networkState.isConnected }
//    }
//
//    private fun networkBackTrigger(
//            animatorSet: AnimatorSet,
//            netWarnIcon: View,
//            backgroundWarn: View,
//            syncableView: SyncableView,
//            networkState: NetworkState): SyncTrigger {
//
//        animatorSet.duration = networkBackAnimDuration
//
//        val flopUpObjAnimator = ObjectAnimator.ofFloat(netWarnIcon, "translationY", 0f, -screenHeightPx / 2f)
//        flopUpObjAnimator.setInterpolator(CustomEasing.flopUp)
//        val backgroundFadeObjAnimator = ObjectAnimator.ofFloat(backgroundWarn, "alpha", 1f, 0f)
//
//        animatorSet.playTogether(
//                flopUpObjAnimator,
//                backgroundFadeObjAnimator)
//        animatorSet.addListener(SyncerAnimationComplete(syncableView, backgroundWarn, netWarnIcon))//onAnimationEnd() -> resetAlpha values, then call syncView()
//
//        return SyncTrigger({
//            // temporarily adjust visibility before running animation, syncView() gets
//            // called at the end of the animation anyway to put everything back to how it should be
//            netWarnIcon.visibility = VISIBLE
//            backgroundWarn.visibility = VISIBLE
//            animatorSet.start()
//        }) { networkState.isConnected }
//    }
//
//    private fun showBusyTrigger(
//            animatorSet: AnimatorSet,
//            busySpinner: View,
//            syncableView: SyncableView,
//            pwned: Pwned): SyncTrigger {
//
//        animatorSet.duration = searchingTransitionAnimDuration
//        animatorSet.playTogether(ObjectAnimator.ofFloat(busySpinner, "alpha", 0f, 1f))
//        animatorSet.addListener(SyncerAnimationComplete(syncableView, busySpinner))//onAnimationEnd() -> resetAlpha values, then call syncView()
//
//        return SyncTrigger({
//            // temporarily adjust visibility before running animation, syncView() gets
//            // called at the end of the animation anyway to put everything back to how it should be
//            busySpinner.visibility = VISIBLE
//            animatorSet.start()
//        }) { pwned.isBusy }
//    }
//
//
//    private fun hideBusyTrigger(
//            animatorSet: AnimatorSet,
//            busySpinner: View,
//            syncableView: SyncableView,
//            pwned: Pwned): SyncTrigger {
//
//        animatorSet.duration = searchingTransitionAnimDuration
//        animatorSet.playTogether(ObjectAnimator.ofFloat(busySpinner, "alpha", 1f, 0f))
//        animatorSet.addListener(SyncerAnimationComplete(syncableView, busySpinner))//onAnimationEnd() -> resetAlpha values, then call syncView()
//
//        return SyncTrigger({
//            // temporarily adjust visibility before running animation, syncView() gets
//            // called at the end of the animation anyway to put everything back to how it should be
//            busySpinner.visibility = VISIBLE
//            animatorSet.start()
//        }) { !pwned.isBusy }
//    }
//
//
//    private fun hideInfoIconTrigger(
//            animatorSet: AnimatorSet,
//            infoIcon: View,
//            syncableView: SyncableView,
//            pwned: Pwned): SyncTrigger {
//
//        animatorSet.duration = searchingTransitionAnimDuration
//        animatorSet.playTogether(
//                ObjectAnimator.ofFloat(infoIcon, "alpha", 1f, 0f),
//                ObjectAnimator.ofFloat(infoIcon, "rotation", 0f, 90f)
//        )
//        animatorSet.addListener(SyncerAnimationComplete(syncableView, infoIcon))//onAnimationEnd() -> resetAlpha values, then call syncView()
//
//
//        return SyncTrigger({ animatorSet.start() }) { pwned.isBusy }
//    }
//
//
//    private fun showInfoIconTrigger(
//            animatorSet: AnimatorSet,
//            infoIcon: View,
//            syncableView: SyncableView,
//            pwned: Pwned): SyncTrigger {
//
//        animatorSet.duration = searchingTransitionAnimDuration
//        animatorSet.playTogether(
//                ObjectAnimator.ofFloat(infoIcon, "alpha", 0f, 1f),
//                ObjectAnimator.ofFloat(infoIcon, "rotation", -90f, 0f)
//        )
//        animatorSet.addListener(SyncerAnimationComplete(syncableView, infoIcon))//onAnimationEnd() -> resetAlpha values, then call syncView()
//
//        return SyncTrigger({
//            // temporarily adjust visibility before running animation, syncView() gets
//            // called at the end of the animation anyway to put everything back to how it should be
//            infoIcon.visibility = if (!pwned.isBusy && pwned.pwnedResult.pwnedState != UNKNOWN) VISIBLE else INVISIBLE
//            animatorSet.start()
//        }) { !pwned.isBusy }
//    }
//
//
//    private fun setupRemoveLoadingBarTrigger(
//            animatorSet: AnimatorSet,
//            initProgress: View,
//            syncableView: SyncableView,
//            pwned: Pwned): SyncTrigger {
//
//        animatorSet.duration = 700
//        animatorSet.playTogether(ObjectAnimator.ofFloat(initProgress, "alpha", 1f, 0f))
//        animatorSet.addListener(SyncerAnimationComplete(syncableView))//onAnimationEnd() -> syncView()
//
//        return SyncTrigger({
//            // temporarily adjust visibility before running animation, syncView() gets
//            // called at the end of the animation anyway to put everything back to how it should be
//            initProgress.visibility = VISIBLE
//            animatorSet.start()
//        }) { pwned.offlineDataLoadingComplete() }
//    }
//
//
//    private fun jiggleTrigger(
//            animatorSet: AnimatorSet,
//            infoIcon: View,
//            cloudIcon: View,
//            syncableView: SyncableView): SyncTrigger {
//
//        animatorSet.duration = 1000
//        animatorSet.playTogether(
//                ObjectAnimator.ofFloat(cloudIcon, "rotation", -jiggleAngle1, jiggleAngle1,
//                        -jiggleAngle1, jiggleAngle2, -jiggleAngle2, 0f),
//                ObjectAnimator.ofFloat(infoIcon, "rotation", -jiggleAngle1, jiggleAngle1,
//                        -jiggleAngle1, jiggleAngle2, -jiggleAngle2, 0f))
//        animatorSet.addListener(SyncerAnimationComplete(syncableView, cloudIcon, infoIcon))//onAnimationEnd() -> syncView()
//
//
//        return SyncTrigger({ animatorSet.start() }) { true }
//    }
//
//    fun checkAll() {
//        networkGoneTrigger.checkLazy()
//        networkBackTrigger.checkLazy()
//        showBusyTrigger.checkLazy()
//        hideBusyTrigger.checkLazy()
//        showInfoTrigger.checkLazy()
//        hideInfoTrigger.checkLazy()
//        removeLoadingBarTrigger.checkLazy()
//        jiggleTrigger.check() //not lazy, we want the animation triggered each time the view is recreated
//    }
//
//    fun busyTransitionAnimationsRunning(): Boolean {
//        return (showBusyAnimSet.isRunning || hideBusyAnimSet.isRunning
//                || showInfoAnimSet.isRunning || hideInfoAnimSet.isRunning)
//    }
//
//    fun removeLoadingBarAnimRunning(): Boolean {
//        return removeLoadingBarAnimSet.isRunning
//    }
//
//    fun networkChangeAnimRunning(): Boolean {
//        return networkGoneAnimSet.isRunning || networkBackAnimSet.isRunning
//    }
//
//    // We're regretably doing it this way as Activity Transitions appear to be a disaster if you go
//    // Activity A -> Activity B -> *rotate screen* -> back to Activity A
//    fun infoIconClicked(
//            topLevelView: ViewGroup,
//            infoIcon: View,
//            busySpinner: View,
//            editText: LockedKeyboardEditText,
//            eyeView: View,
//            warningText: View,
//            warningDetail: View,
//            cloudIcon: View,
//            aboutBackground: View,
//            successCallBack: SuccessCallBack) {
//
//        //current location of infoIcon
//        infoIcon.rotation = 0f//if it is in the middle of jiggling, the location is wrong
//        val offsetViewBounds = Rect()
//        infoIcon.getDrawingRect(offsetViewBounds)
//        // calculates the relative coordinates to the parent
//        topLevelView.offsetDescendantRectToMyCoords(infoIcon, offsetViewBounds)
//        val x = offsetViewBounds.left
//        val y = offsetViewBounds.top
//
//        val targetX = ((screenWidthPx - infoIcon.width.toFloat() * 1f) / 2f).toInt()//1.23 because of the scale difference
//        val targetY = (8f * densityScalingFactor).toInt()//top padding in About screen is 10dp so I don't know why 8 works better here
//
//        //where we want to be with infoIcon
//        val xChange = targetX - x
//        val yChange = targetY - y
//
//        infoIcon.isEnabled = false
//        // because if a user closes the keyboard by pressing back at this point,
//        // all the animations are borked
//        editText.isKeyboardLockedOpen = true
//
//        val animatorSet = AnimatorSet()
//        animatorSet.duration = clearScreenDuration
//        animatorSet.interpolator = CustomEasing.innieOutie
//
//        val warningDetailAnimator = ObjectAnimator.ofFloat(warningDetail, "translationX", 0f, -screenWidthPx)
//        warningDetailAnimator.setStartDelay(0)
//        val warningTextAnimator = ObjectAnimator.ofFloat(warningText, "translationX", 0f, -screenWidthPx)
//        warningTextAnimator.setStartDelay(50)
//        val editTextAnimator = ObjectAnimator.ofFloat(editText, "translationX", 0f, -screenWidthPx)
//        editTextAnimator.setStartDelay(100)
//        val eyeViewAnimator = ObjectAnimator.ofFloat(eyeView, "translationX", 0f, -screenWidthPx)
//        eyeViewAnimator.setStartDelay(100)
//        val cloudAnimator = ObjectAnimator.ofFloat(cloudIcon, "translationX", 0f, screenWidthPx)
//        cloudAnimator.setStartDelay(150)
//        val infoAnimator1 = ObjectAnimator.ofFloat(infoIcon, "translationY", 0f, yChange.toFloat())
//        infoAnimator1.setStartDelay(250)
//        val infoAnimator2 = ObjectAnimator.ofFloat(infoIcon, "translationX", 0f, xChange.toFloat())
//        infoAnimator2.setStartDelay(250)
//        val infoAnimator3 = ObjectAnimator.ofFloat(infoIcon, "scaleX", 1f, 0.7f, 0.7f, 1.1f)
//        infoAnimator3.startDelay = 250
//        val infoAnimator4 = ObjectAnimator.ofFloat(infoIcon, "scaleY", 1f, 0.7f, 0.7f, 1.1f)
//        infoAnimator4.startDelay = 250
//        val backgroundAnimator = ObjectAnimator.ofFloat(aboutBackground, "alpha", 0f, 1f)
//        backgroundAnimator.setStartDelay(250)
//
//
//        animatorSet.playTogether(
//                ObjectAnimator.ofFloat(busySpinner, "translationX", 0f, -screenWidthPx),
//                warningDetailAnimator,
//                warningTextAnimator,
//                editTextAnimator,
//                eyeViewAnimator,
//                cloudAnimator,
//                infoAnimator1,
//                infoAnimator2,
//                infoAnimator3,
//                infoAnimator4,
//                backgroundAnimator)
//        animatorSet.addListener(object : AnimatorListenerAdapter() {
//            override fun onAnimationEnd(animation: Animator) {
//                super.onAnimationEnd(animation)
//                successCallBack.success()
//
//                //yukky reset stuff
//                Handler().postDelayed({
//
//                    infoIcon.isEnabled = true
//                    editText.isKeyboardLockedOpen = false
//
//                    infoIcon.translationX = 0f
//                    infoIcon.translationY = 0f
//                    infoIcon.scaleX = 1f
//                    infoIcon.scaleY = 1f
//                    editText.translationX = 0f
//                    eyeView.translationX = 0f
//                    cloudIcon.translationX = 0f
//                    busySpinner.translationX = 0f
//                    warningText.translationX = 0f
//                    warningDetail.translationX = 0f
//                    aboutBackground.alpha = 0f
//
//                }, 1000)//need to allow time for the about activity to start
//
//            }
//        })
//        animatorSet.start()
//    }
//
//}
//
