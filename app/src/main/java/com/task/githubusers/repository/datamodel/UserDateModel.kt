package com.task.githubusers.repository.datamodel

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.task.githubusers.repository.models.User
import com.task.githubusers.repository.models.UserErrorResponse
import com.task.githubusers.repository.models.UserResponse
import com.task.githubusers.repository.server.GithubUserAPI
import com.task.githubusers.ui.users.UserViewModel
import com.task.githubusers.utils.extension.ViewState
import com.task.githubusers.utils.github.LIMIT_REACHED_RESPONSE_CODE
import okhttp3.ResponseBody
import retrofit2.Response
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Muhammad Maqsood on 20/04/2022.
 */

@Singleton
class UserDateModel @Inject constructor(private val githubUserAPI: GithubUserAPI) {

    // keep the list of all results received
    private val inMemoryCache = mutableListOf<User>()

    private var lastRequestedPage = 0
    var isRequestInProgress = false

    fun fetchUsers(
        accessToken: String,
        requestType: UserViewModel.RequestType
    ): Observable<ViewState> {

        /**
         * If we are loading data and its already available in memory, then return it instead
         * of downloading again.
         * Above condition does not applicable for pull to refresh and load more.
         */
        return when (requestType) {
            UserViewModel.RequestType.LOAD -> {
                if (inMemoryCache.size > 0) {
                    Observable.from(inMemoryCache).map {
                        ViewState.DataLoaded(0, inMemoryCache, requestType)
                    }
                } else getUsers(accessToken, requestType)
            }
            UserViewModel.RequestType.PULL_TO_REFRESH -> getUsers(accessToken, requestType)
            UserViewModel.RequestType.LOAD_MORE -> requestMore(accessToken, requestType)
        }
    }

    private fun getUsers(
        accessToken: String,
        requestType: UserViewModel.RequestType
    ): Observable<ViewState> {
        lastRequestedPage = 0
        inMemoryCache.clear()
        return loadRemote(accessToken, lastRequestedPage)
            .map {
                return@map interceptResponse(lastRequestedPage, it, requestType)
            }
    }

    private fun requestMore(
        accessToken: String,
        requestType: UserViewModel.RequestType
    ): Observable<ViewState> {
        return loadRemote(accessToken, lastRequestedPage + 1)
            .map {
                val response = interceptResponse(lastRequestedPage, it, requestType)
                if (it.isSuccessful) {
                    lastRequestedPage++
                }
                return@map response
            }
    }

    private fun loadRemote(
        accessToken: String,
        page: Int
    ): Observable<Response<UserResponse>> {
        return githubUserAPI.getUsers(
            header = accessToken,
            page = page,
            perPage = NETWORK_PAGE_SIZE
        )
    }

    private fun interceptResponse(
        page: Int,
        response: Response<UserResponse>,
        requestType: UserViewModel.RequestType
    ): ViewState {
        val userResponse = if (response.isSuccessful) {
            val fetchedUsers = response.body()?.users
            if (fetchedUsers != null) {
                inMemoryCache.addAll(fetchedUsers)
            }
            ViewState.DataLoaded(
                page,
                fetchedUsers ?: listOf<User>(), requestType
            )
        } else {
            val throwable = interceptError(response.errorBody())
            if (response.code() == LIMIT_REACHED_RESPONSE_CODE) {
                ViewState.LimitReached(requestType, throwable)
            } else
                ViewState.StateError(throwable)
        }
        return userResponse
    }

    companion object {
        const val VISIBLE_THRESHOLD = 2
        const val NETWORK_PAGE_SIZE = 10

        fun interceptError(responseBody: ResponseBody?): Throwable {
            val gson = Gson()
            val type = object : TypeToken<UserErrorResponse>() {}.type
            val errorResponse: UserErrorResponse? =
                gson.fromJson(responseBody?.charStream(), type)
            return Throwable(errorResponse?.message)
        }
    }
}