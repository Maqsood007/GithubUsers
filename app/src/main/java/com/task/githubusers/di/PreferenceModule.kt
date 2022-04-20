package com.task.githubusers.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.task.githubusers.utils.AppConstants.SHARED_PREF
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Created by Muhammad Maqsood on 20/04/2022.
 */
@Module
@InstallIn(SingletonComponent::class)
object PreferenceModule {

    @Provides
    fun providePreference(application: Application): SharedPreferences {
        return application.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
    }
}