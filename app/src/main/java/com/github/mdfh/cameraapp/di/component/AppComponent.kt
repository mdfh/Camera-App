package com.github.mdfh.cameraapp.di.component


import com.github.mdfh.cameraapp.CameraApp
import com.github.mdfh.cameraapp.di.builder.ActivityBuilder
import com.github.mdfh.cameraapp.di.module.AppModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

/**
 * Created by Faraz on 12/17/2017.
 */
@Singleton
//@Component(modules = [AppModule::class, AndroidInjectionModule::class.java])
@Component(modules = [(AndroidInjectionModule::class), (AppModule::class),
    (ActivityBuilder::class)])
interface AppComponent
{
    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: CameraApp): Builder

        fun build(): AppComponent

    }

    fun inject(cameraApp: CameraApp)
}