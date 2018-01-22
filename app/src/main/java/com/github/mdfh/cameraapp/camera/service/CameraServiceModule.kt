package com.github.mdfh.cameraapp.camera.service

import dagger.Module
import dagger.Provides



/**
 * Created by Faraz on 1/22/2018.
 */
@Module
class CameraServiceModule {
    @Provides
    internal fun provideCameraService() : CameraService
    {
        return CameraService()
    }
}