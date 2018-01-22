/*
 *  Copyright (C) 2018 KASHIF
 *
 *  */

package com.github.mdfh.cameraapp.data

import android.net.Uri
import com.eccyan.optional.Optional
import com.github.mdfh.cameraapp.data.local.db.DbHelper
import com.github.mdfh.cameraapp.data.local.pref.PreferencesHelper
import com.github.mdfh.cameraapp.data.remote.ApiHelper
import io.reactivex.Observable

/**
 * Created by Faraz on 10/01/2018.
 */

interface DataManager : DbHelper, PreferencesHelper, ApiHelper
{

}
