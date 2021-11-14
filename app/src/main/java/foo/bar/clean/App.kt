package foo.bar.clean

import androidx.multidex.MultiDexApplication
import clean.BuildConfig
import co.early.fore.kt.core.delegate.DebugDelegateDefault
import co.early.fore.kt.core.delegate.Fore
import foo.bar.clean.di.dataModule
import foo.bar.clean.di.domainModule
import foo.bar.clean.di.uiModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Copyright © 2019 early.co. All rights reserved.
 */
@ExperimentalStdlibApi
class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        inst = this

        if (BuildConfig.DEBUG) {
            Fore.setDelegate(DebugDelegateDefault(tagPrefix = "clean_"))
        }

        startKoin {
            if (BuildConfig.DEBUG) {
                // Use Koin Android Logger
                androidLogger()
            }
            // declare Android context
            androidContext(this@App)
            // declare modules to use
            modules(
                listOf(
                    dataModule,
                    domainModule,
                    uiModule
                )
            )
        }

        init()
    }

    companion object {
        lateinit var inst: App private set

        fun init() {
            // run any initialisation code here

//            val persista: PerSista = inst.get()
//            persista.wipeEverything {}
        }
    }
}
