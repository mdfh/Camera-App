/*
 *  Copyright (C) 2018 KASHIF
 *
 *  */

package com.github.mdfh.cameraapp.di.builder;

import com.github.mdfh.cameraapp.camera.service.CameraService;
import com.github.mdfh.cameraapp.camera.service.CameraServiceModule;
import com.github.mdfh.cameraapp.ui.main.MainActivity;
import com.github.mdfh.cameraapp.ui.main.MainActivityModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by Faraz on 10/01/2018.
 */
@Module
public abstract class ActivityBuilder
{
    @ContributesAndroidInjector(modules = MainActivityModule.class)
    abstract MainActivity bindMainActivity();

    @ContributesAndroidInjector(modules = CameraServiceModule.class)
    abstract CameraService bindCallReceiver();
}
