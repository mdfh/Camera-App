package com.nextgenxapps.callfilter.dagger

import javax.inject.Qualifier


/**
 * Created by Faraz on 12/18/2017.
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ActivityContext

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiInfo

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class DatabaseInfo

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class PreferenceInfo