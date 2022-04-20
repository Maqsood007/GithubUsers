package com.task.githubusers.repository.preference

import android.content.SharedPreferences
import javax.inject.Inject

/**
 * Created by Muhammad Maqsood on 20/04/2022.
 */
class AppPreferencesImpl @Inject constructor(private val sharedPreferences: SharedPreferences) :
    AppPreferences {
}