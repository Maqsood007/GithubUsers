package com.task.githubusers.utils.github

import java.util.concurrent.TimeUnit

/**
 * Created by Muhammad Maqsood on 23/04/2022.
 */
object GithubAuthentication {
    const val CLIENT_ID = "9473dbb421797e36779d"
    const val CLIENT_SECRET = "68eba2d0b0688ad852e08ee711cc245d92604a17"
    const val REDIRECT_URI = "https://github.com/Maqsood007/callback"
    const val SCOPE = "read:user,user:email"
    const val AUTH_URL = "https://github.com/login/oauth/authorize"
    const val TOKEN_URL = "https://github.com/login/oauth/access_token"
}

const val LIMIT_REACHED_RESPONSE_CODE = 403

val state = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())

val githubAuthURLFull =
    GithubAuthentication.AUTH_URL + "?client_id=" + GithubAuthentication.CLIENT_ID + "&scope=" +
            GithubAuthentication.SCOPE + "&redirect_uri=" +
            GithubAuthentication.REDIRECT_URI + "&state=" + state
