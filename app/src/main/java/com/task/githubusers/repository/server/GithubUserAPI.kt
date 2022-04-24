package com.task.githubusers.repository.server

import com.task.githubusers.repository.models.AccessTokenResponse
import com.task.githubusers.repository.models.UserResponse
import com.task.githubusers.utils.github.GithubAuthentication
import com.task.githubusers.utils.github.GithubAuthentication.TOKEN_URL
import retrofit2.Response
import retrofit2.http.*
import rx.Observable

/**
 * Created by Muhammad Maqsood on 20/04/2022.
 */
interface GithubUserAPI {

    @GET("search/users?q=followers:>=0&sort:followers")
    fun getUsers(
        @Header("Authorization") header : String,
        @Query("o") order: String = "desc",
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Observable<Response<UserResponse>>

    @Headers("Accept: application/json")
    @POST(TOKEN_URL)
    @FormUrlEncoded
    fun getAccessToken(
        @Field("grant_type") grantType: String = "authorization_code",
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String = GithubAuthentication.REDIRECT_URI,
        @Field("client_id") clientId: String = GithubAuthentication.CLIENT_ID,
        @Field("client_secret") clientSecret: String = GithubAuthentication.CLIENT_SECRET,
    ): Observable<Response<AccessTokenResponse>>
}