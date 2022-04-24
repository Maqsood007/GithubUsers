package com.task.githubusers.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.task.githubusers.repository.datamodel.LoginRepoModel
import com.task.githubusers.repository.models.AccessTokenResponse
import com.task.githubusers.ui.users.UserViewModel
import com.task.githubusers.utils.extension.safeGet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.RENDEZVOUS
import kotlinx.coroutines.launch
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by Muhammad Maqsood on 24/04/2022.
 */
@HiltViewModel
class GithubLoginViewModel @Inject constructor(private val loginRepoModel: LoginRepoModel) :
    ViewModel() {

    private val subscriptions: CompositeSubscription = CompositeSubscription()
    val loginStateLiveData = LoginStateLiveData()

    val accessTokenChannel = Channel<String>(RENDEZVOUS)

    /**
     * Get users by query, perform API call
     */
    fun getAccessToken(githubCode: String) {
        val s = loginRepoModel.getAccessToken(code = githubCode)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { }
            .subscribe(
                { setAccessToken(accessTokenResponse = it.body()) },
                { setError(throwable = it) })
        subscriptions.add(s)
    }

    private fun setAccessToken(accessTokenResponse: AccessTokenResponse?) {
        loginRepoModel.setAccessToken(accessTokenResponse?.access_token.safeGet())
        viewModelScope.launch {
            accessTokenChannel.send(accessTokenResponse?.access_token.safeGet())
        }
        loginStateLiveData.value =
            LoginViewState.StateDataLoaded(accessToken = accessTokenResponse?.access_token.safeGet())
    }

    private fun setError(throwable: Throwable) {
        loginStateLiveData.value = LoginViewState.StateError(throwable)
    }

    override fun onCleared() {
        super.onCleared()
        subscriptions.unsubscribe()
    }

}