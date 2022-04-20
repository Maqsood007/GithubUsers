package com.task.githubusers.repository.server

import com.task.githubusers.repository.models.UserResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Muhammad Maqsood on 20/04/2022.
 */
interface GithubUserAPI {

    @GET("search/users")
    fun getUsers(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): UserResponse
}