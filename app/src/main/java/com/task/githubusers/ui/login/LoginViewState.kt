package com.task.githubusers.ui.login

import androidx.lifecycle.MutableLiveData

/**
 * Created by Muhammad Maqsood on 24/04/2022.
 */
sealed class LoginViewState {
    object StateIdle : LoginViewState()
    data class StateError(val throwable: Throwable) : LoginViewState()
    data class StateDataLoaded(val accessToken: String) : LoginViewState()
}

/**
 * ViewState LiveData - to init default state value
 */
class LoginStateLiveData(state: LoginViewState = LoginViewState.StateIdle) :
    MutableLiveData<LoginViewState>() {
    init {
        value = state
    }
}