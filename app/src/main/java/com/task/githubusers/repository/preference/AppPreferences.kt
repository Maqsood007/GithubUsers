package com.task.githubusers.repository.preference

/**
 * Created by Muhammad Maqsood on 20/04/2022.
 */
interface AppPreferences {

    fun setAccessToken(token: String)
    fun getAccessToken(): String
    fun isUserLogged(): Boolean

    companion object {
        const val KEY_GITHUB_ACCESS_TOKEN = "KEY_GITHUB_ACCESS_TOKEN"
    }

}