package com.github.mdfh.cameraapp.di.module

import android.content.Context
import com.eccyan.optional.Optional
import com.github.mdfh.cameraapp.CameraApp
import com.github.mdfh.cameraapp.PREF_NAME
import com.github.mdfh.cameraapp.camera.CameraPreview
import com.github.mdfh.cameraapp.data.AppDataManager
import com.github.mdfh.cameraapp.data.DataManager
import com.github.mdfh.cameraapp.data.local.db.AppDbHelper
import com.github.mdfh.cameraapp.data.local.db.DbHelper
import com.github.mdfh.cameraapp.data.local.pref.PreferencesHelper
import com.google.gson.Gson
import com.nextgenxapps.callfilter.dagger.DatabaseInfo
import com.nextgenxapps.callfilter.dagger.PreferenceInfo
import com.nextgenxapps.callfilter.data.local.pref.AppPreferencesHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


/**
 * Created by Faraz on 12/17/2017.
 */
@Module
class AppModule {

    @Provides
    @Singleton
    internal fun provideContext(application: CameraApp): Context {
        return application
    }

    @Provides
    @Singleton
    internal fun provideDbHelper(appDbHelper: AppDbHelper): DbHelper {
        return appDbHelper
    }

    @Provides
    @Singleton
    internal fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    internal fun providePreferencesHelper(appPreferencesHelper: AppPreferencesHelper): PreferencesHelper {
        return appPreferencesHelper
    }

    /*@Provides
    @Singleton
    internal fun provideCameraPreview(cameraPreview: CameraPreview): CameraPreview {
        return cameraPreview
    }*/

    @Provides
    @DatabaseInfo
    fun provideDatabaseName(): String {
        return "cameraapp.db"
    }

    @Provides
    @DatabaseInfo
    fun provideDatabaseVersion(): Int? {
        return 1
    }

    @Provides
    @PreferenceInfo
    internal fun providePreferenceName(): String {
        return PREF_NAME
    }


    @Provides
    @Singleton
    internal fun provideDataManager(dataManager: AppDataManager): DataManager {
        return dataManager
    }
}