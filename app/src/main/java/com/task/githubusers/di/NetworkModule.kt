package com.task.githubusers.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by Muhammad Maqsood on 20/04/2022.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val baseUrl = "https://api.github.com/"
    private const val connectionTimeout = 60L //in seconds
    private const val readTimeout = 60L //in seconds
    private const val writeTimeout = 120L //in seconds

    @Provides
    fun okHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor {
                val request = it.request()
                val newUrl: HttpUrl?
                newUrl = request.url.newBuilder()
                    .build()
                it.proceed(
                    request.newBuilder()
                        .url(newUrl)
                        .build()
                )
            }
            .connectTimeout(connectionTimeout, TimeUnit.SECONDS)
            .readTimeout(readTimeout, TimeUnit.SECONDS)
            .writeTimeout(writeTimeout, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    fun converterFactory(gson: Gson): Converter.Factory = GsonConverterFactory.create(gson)

    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    fun retrofit(httpClient: OkHttpClient, converterFactory: Converter.Factory): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient)
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(converterFactory)
            .build()
    }
}
