package com.task.githubusers.repository.server

import com.task.githubusers.repository.models.UserResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

/**
 * Created by Muhammad Maqsood on 20/04/2022.
 */
interface GithubUserAPI {

    @GET("search/users?q=followers:>=0&sort:followers")
    fun getUsers(
        @Query("o") order: String = "desc",
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Observable<Response<UserResponse>>
}