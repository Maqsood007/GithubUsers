package com.task.githubusers.ui.users

import androidx.lifecycle.ViewModel
import com.task.githubusers.repository.datamodel.UserDateModel
import com.task.githubusers.repository.models.User
import com.task.githubusers.utils.extension.StateLiveData
import com.task.githubusers.utils.extension.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by Muhammad Maqsood on 20/04/2022.
 */
@HiltViewModel
class UserViewModel @Inject constructor(private val userDateModel: UserDateModel) : ViewModel() {

    private val subscriptions: CompositeSubscription = CompositeSubscription()
    val state = StateLiveData()
    val mostFollowedQuery = "followers:>=0&sort:followers"

    /**
     * Get users by query, perform API call
     */
    fun search(query: String, page: Int = 0, perPage: Int = 10, requestType: RequestType) {
        if (isDataLoaded(requestType, page)) return
        val s = getRemoteData(query, page, perPage)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { setLoadingState(requestType) }
            .subscribe({ setDataState(page, users = it) }, { setErrorState(throwable = it) })
        subscriptions.add(s)
    }

    private fun isDataLoaded(requestType: RequestType, page: Int): Boolean {
        val currentValue = state.value
        return currentValue is ViewState.DataLoaded
                && currentValue.page == page
                && requestType == RequestType.LOAD
    }

    private fun getRemoteData(query: String, page: Int, perPage: Int): Observable<List<User>> =
        userDateModel
            .getUsers(query, page, perPage)

    private fun setLoadingState(requestType: RequestType) {
        state.value =
            if (requestType == RequestType.LOAD) ViewState.StateProgress else ViewState.StatePullToRefresh
    }

    private fun setDataState(page: Int, users: List<User>) {
        state.value = ViewState.DataLoaded(page, users)
    }

    private fun setErrorState(throwable: Throwable) {
        state.value = ViewState.StateError(throwable)
    }

    override fun onCleared() {
        super.onCleared()
        subscriptions.unsubscribe()
    }

    enum class RequestType {
        LOAD,
        PULL_TO_REFRESH
    }

}