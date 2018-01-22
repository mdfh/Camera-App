/*
 *  Copyright (C) 2018 KASHIF
 *
 *  */

package com.nextgenxapps.callfilter.data.local.pref

import android.content.Context
import android.content.SharedPreferences
import com.github.mdfh.cameraapp.data.local.pref.PreferencesHelper

import com.google.gson.Gson
import com.nextgenxapps.callfilter.dagger.PreferenceInfo

import javax.inject.Inject

/**
 * Created by Faraz on 10/01/2018.
 */

class AppPreferencesHelper @Inject
constructor(context: Context,
            @PreferenceInfo prefFileName: String,
            private val mGson: Gson) : PreferencesHelper {

    private val mPrefs: SharedPreferences = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE)


    companion object {

        private val PREF_KEY_REGISTERED_USER = "PREF_KEY_USER_LOGGED_IN_MODE"
    }
}
