package com.github.mdfh.cameraapp

import android.app.Activity
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.support.multidex.MultiDexApplication
import com.eccyan.optional.Optional
import com.github.mdfh.cameraapp.di.component.AppComponent
import com.github.mdfh.cameraapp.di.component.DaggerAppComponent
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import dagger.android.*
import javax.inject.Inject

/**
 * Created by Faraz on 1/22/2018.
 */

class CameraApp  : MultiDexApplication(), HasActivityInjector,
        HasBroadcastReceiverInjector, HasServiceInjector {


    lateinit var appComponent: AppComponent

    private fun initDagger(app: CameraApp) {
        appComponent = DaggerAppComponent.builder()
                .application(this)
                .build()
        appComponent.inject(this)
    }

    @Inject
    lateinit var broadcastReceiverInjector: DispatchingAndroidInjector<BroadcastReceiver>

    @Inject
    lateinit var serviceInjector: DispatchingAndroidInjector<Service>

    @Inject
    lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun serviceInjector(): AndroidInjector<Service> {
        return serviceInjector
    }

    override fun onCreate() {
        super.onCreate()
        initDagger(this)
    }

    override fun broadcastReceiverInjector(): AndroidInjector<BroadcastReceiver> {
        return broadcastReceiverInjector
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return activityDispatchingAndroidInjector
    }
}
