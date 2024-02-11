package com.recipe

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.recipe.di.dataSourceModule
import com.recipe.di.initKoinKmm
import com.recipe.di.viewModelsModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import java.lang.ref.WeakReference

class RecipeApp : Application() {
    companion object {
        lateinit var appContext: Context
    }
    object CurrentActivityHolder {
        private var currentActivityReference = WeakReference<Activity>(null)

        var currentActivity: Activity?
            get() = currentActivityReference.get()
            set(value) {
                currentActivityReference = WeakReference(value)
            }
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                CurrentActivityHolder.currentActivity = activity
            }

            override fun onActivityStarted(activity: Activity) {

            }

            override fun onActivityResumed(activity: Activity) {
            }

            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
                CurrentActivityHolder.currentActivity = null
            }

        })

        initKoinKmm {
            androidContext(appContext)
            androidLogger()
            modules(dataSourceModule(),viewModelsModule)
        }
    }
}