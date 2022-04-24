package com.task.githubusers.ui.users

import android.app.Application
import androidx.lifecycle.ViewModel
import com.task.githubusers.R
import com.task.githubusers.repository.datamodel.LoginRepoModel
import com.task.githubusers.repository.datamodel.UserDateModel
import com.task.githubusers.utils.NetworkUtils
import com.task.githubusers.utils.extension.StateLiveData
import com.task.githubusers.utils.extension.StringsExt
import com.task.githubusers.utils.extension.ViewState
import com.task.githubusers.utils.extension.safeGet
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by Muhammad Maqsood on 20/04/2022.
 */
@HiltViewModel
class UserViewModel @Inject constructor(
    private val application: Application,
    private val userDateModel: UserDateModel,
    private val loginDataModel: LoginRepoModel
) : ViewModel() {

    private val subscriptions: CompositeSubscription = CompositeSubscription()
    val state = StateLiveData()
    private var lastFailedRequestType: RequestType? = null

    /**
     * Get users by query, perform API call
     */
    fun fetchUserByMostFollowed(requestType: RequestType) {

        //check if network available
        if (NetworkUtils.isInternetAvailable(application).not()) {
            state.value =
                ViewState.StateError(Throwable(application.getString(R.string.network_not_available)))
            return
        }

        val s = userDateModel
            .fetchUsers(getAuthorisationHeader(), requestType)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { setLoadingState(requestType) }
            .subscribe(
                { setDataState(viewState = it) },
                { setErrorState(throwable = it) })
        subscriptions.add(s)
    }

    private fun setLoadingState(requestType: RequestType) {
        state.value =
            when (requestType) {
                RequestType.LOAD -> ViewState.StateProgress
                else -> ViewState.StatePullToRefresh
            }
    }

    private fun setDataState(viewState: ViewState) {
        state.value = viewState
        setDataLoading(isLoading = false)
    }

    private fun setErrorState(throwable: Throwable) {
        state.value = ViewState.StateError(throwable)
        setDataLoading(isLoading = false)
    }

    fun setLastFailedRequestType(requestType: RequestType) {
        lastFailedRequestType = requestType
    }

    fun getLastFailedRequestType() = lastFailedRequestType

    fun setDataLoading(isLoading: Boolean) {
        userDateModel.isRequestInProgress = isLoading
    }

    fun isDataLoading() = userDateModel.isRequestInProgress


    private fun getAuthorisationHeader(): String {
        return if (isUserAvailable()) "token ${loginDataModel.getAccessToken()}" else StringsExt.empty().safeGet()
    }

    fun isUserAvailable() = loginDataModel.isUserLogin()

    override fun onCleared() {
        super.onCleared()
        subscriptions.unsubscribe()
    }

    enum class RequestType {
        LOAD,
        PULL_TO_REFRESH,
        LOAD_MORE
    }

}