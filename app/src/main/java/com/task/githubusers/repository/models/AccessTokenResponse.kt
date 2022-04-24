package com.task.githubusers.repository.models

/**
 * Response to get github authentication access token
 */
data class AccessTokenResponse(
    val access_token: String?,
    val scope: String?,
    val token_type: String?
)