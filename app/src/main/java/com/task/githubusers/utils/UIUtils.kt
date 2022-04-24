package com.task.githubusers.utils

import android.content.Context
import android.widget.Toast

/**
 * Created by Muhammad Maqsood on 24/04/2022.
 */
object UIUtils {

    fun showToast(context: Context, message: String?, length: Int = Toast.LENGTH_LONG) {
        message?.let { msg ->
            Toast.makeText(context, msg, length).show()
        }
    }
}