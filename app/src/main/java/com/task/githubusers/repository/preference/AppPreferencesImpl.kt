package com.task.githubusers.repository.preference

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.task.githubusers.repository.preference.AppPreferences.Companion.KEY_GITHUB_ACCESS_TOKEN
import com.task.githubusers.utils.extension.StringsExt
import com.task.githubusers.utils.extension.safeGet
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Muhammad Maqsood on 20/04/2022.
 */
@Singleton
class AppPreferencesImpl @Inject constructor(private val sharedPreferences: SharedPreferences) :
    AppPreferences {

    @SuppressLint("ApplySharedPref")
    override fun setAccessToken(token: String) {
        sharedPreferences.edit().apply {
            putString(KEY_GITHUB_ACCESS_TOKEN, token)
        }.commit()
    }

    override fun getAccessToken(): String {
        return sharedPreferences.getString(KEY_GITHUB_ACCESS_TOKEN, StringsExt.empty()).safeGet()
    }

    override fun isUserLogged(): Boolean {
        return getAccessToken().isEmpty().not()
    }
}