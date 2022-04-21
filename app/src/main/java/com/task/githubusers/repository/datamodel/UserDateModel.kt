package com.task.githubusers.repository.datamodel

import com.task.githubusers.repository.models.User
import com.task.githubusers.repository.server.GithubUserAPI
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Muhammad Maqsood on 20/04/2022.
 */

@Singleton
class UserDateModel @Inject constructor(private val githubUserAPI: GithubUserAPI) {

    fun getUsers(query: String, page: Int, perPage: Int): Observable<List<User>> =
        githubUserAPI.getUsers(page = page, perPage = perPage)
            .map {
                if (it.isSuccessful) it.body()?.users ?: emptyList() else emptyList()
            }
}