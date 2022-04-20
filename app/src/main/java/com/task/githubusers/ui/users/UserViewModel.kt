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

    /**
     * Get users by query, perform API call
     */
    fun search(query: String, page: Int = 0, perPage: Int = 10) {
        val currentValue = state.value
        if (currentValue is ViewState.DataLoaded && currentValue.query == query) return
        val s = getRemoteData(query, page, perPage)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { state.value = ViewState.StateProgress }
            .subscribe({ state.value = ViewState.DataLoaded(query, it) }, {
                state.value =
                    ViewState.StateError(it)
            })
        subscriptions.add(s)
    }

    private fun getRemoteData(query: String, page: Int, perPage: Int): Observable<List<User>> =
        userDateModel
            .getUsers(query, page, perPage)

    override fun onCleared() {
        super.onCleared()
        subscriptions.unsubscribe()
    }

}