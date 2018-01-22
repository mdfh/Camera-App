/*
 *  Copyright (C) 2018 KASHIF
 *
 *  */

package com.github.mdfh.cameraapp.data

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import com.eccyan.optional.Optional
import com.github.mdfh.cameraapp.data.local.db.DbHelper
import com.github.mdfh.cameraapp.data.local.pref.PreferencesHelper
import com.github.mdfh.cameraapp.data.remote.ApiHelper
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Faraz on 10/01/2018.
 */
@Singleton
class AppDataManager @Inject
constructor(private val mContext: Context,
            private val mDbHelper: DbHelper,
            private val mPreferencesHelper: PreferencesHelper,
            private val mApiHelper: ApiHelper) : DataManager {

}
