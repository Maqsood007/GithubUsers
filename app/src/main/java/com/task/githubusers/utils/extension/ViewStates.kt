package com.task.githubusers.utils.extension

import androidx.lifecycle.MutableLiveData
import com.task.githubusers.ui.users.UserViewModel

/**
 * Created by Muhammad Maqsood on 20/04/2022.
 */

/**
 * General State of all Views: Idle, Progress, Error
 */
sealed class ViewState {
    object StateIdle : ViewState()
    object StateProgress : ViewState()
    object StatePullToRefresh: ViewState()
    data class StateError(val throwable: Throwable) : ViewState()
    data class DataLoaded(val page: Int, val users: Any, val requestType: UserViewModel.RequestType) : ViewState()
    data class LimitReached(val requestType: UserViewModel.RequestType, val throwable: Throwable) : ViewState()
    object LoginUser : ViewState()
}

/**
 * ViewState LiveData - to init default state value
 */
class StateLiveData(state: ViewState = ViewState.StateIdle) :
    MutableLiveData<ViewState>() {
    init {
        value = state
    }
}