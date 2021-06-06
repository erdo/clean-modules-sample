package foo.bar.example.clean

import androidx.multidex.MultiDexApplication
import clean.BuildConfig
import co.early.fore.kt.core.delegate.DebugDelegateDefault
import co.early.fore.kt.core.delegate.ForeDelegateHolder
import co.early.persista.PerSista
import foo.bar.example.clean.di.dataModule
import foo.bar.example.clean.di.domainModule
import foo.bar.example.clean.di.uiModule
import org.koin.android.ext.android.get
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
            ForeDelegateHolder.setDelegate(DebugDelegateDefault(tagPrefix = "clean_"))
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
