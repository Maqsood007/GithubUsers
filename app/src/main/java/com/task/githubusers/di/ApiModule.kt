package com.task.githubusers.di

import com.task.githubusers.repository.server.GithubUserAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

/**
 * Created by Muhammad Maqsood on 20/04/2022.
 */
@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    internal fun githubUserAPI(retrofit: Retrofit): GithubUserAPI = retrofit.create(
        GithubUserAPI::class.java
    )
}