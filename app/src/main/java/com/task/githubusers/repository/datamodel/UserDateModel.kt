package com.task.githubusers.repository.datamodel

import com.task.githubusers.repository.server.GithubUserAPI
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Muhammad Maqsood on 20/04/2022.
 */

@Singleton
class UserDateModel @Inject constructor(private val githubUserAPI: GithubUserAPI) {

    fun getUsers(query: String, page: Int, perPage: Int) =
        githubUserAPI.getUsers(query, page, perPage)
}