package com.task.githubusers.utils.extension

/**
 * Created by Muhammad Maqsood on 20/04/2022.
 */

object StringsExt {
    fun empty() = ""
}

fun String?.safeGet() = this ?: ""

