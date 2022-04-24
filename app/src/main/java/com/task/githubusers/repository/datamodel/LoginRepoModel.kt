package com.task.githubusers.repository.datamodel

import com.task.githubusers.repository.models.AccessTokenResponse
import com.task.githubusers.repository.preference.AppPreferences
import com.task.githubusers.repository.server.GithubUserAPI
import retrofit2.Response
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Muhammad Maqsood on 24/04/2022.
 */
@Singleton
class LoginRepoModel @Inject constructor(
    private val githubUserAPI: GithubUserAPI,
    private val appPreferences: AppPreferences
) {

    fun getAccessToken(code: String): Observable<Response<AccessTokenResponse>> {
        return githubUserAPI.getAccessToken(code = code)
    }

    fun getAccessToken() = appPreferences.getAccessToken()

    fun setAccessToken(accessToken: String) = appPreferences.setAccessToken(accessToken)

    fun isUserLogin(): Boolean = appPreferences.isUserLogged()
}