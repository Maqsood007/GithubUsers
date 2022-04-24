package com.task.githubusers.ui.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.task.githubusers.R
import com.task.githubusers.databinding.FragmentUserBinding
import com.task.githubusers.repository.datamodel.UserDateModel.Companion.VISIBLE_THRESHOLD
import com.task.githubusers.repository.models.User
import com.task.githubusers.ui.login.GithubLoginViewModel
import com.task.githubusers.ui.users.details.UserDetailsFragment
import com.task.githubusers.ui.users.details.UserDetailsFragment.Companion.TOP_TITLE
import com.task.githubusers.utils.UIUtils
import com.task.githubusers.utils.extension.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserFragment : Fragment() {

    //region VARIABLES
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var binding: FragmentUserBinding
    private var adapter: UserListAdapter = UserListAdapter(::showUserDetails, ::loginUser, ::isUserAvailable)
    private val githubLoginViewModel by activityViewModels<GithubLoginViewModel>()
    //endregion

    // region LIFECYCLE
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initAdapter()
        initialization()
        initPullToRefresh()
        initLoadMoreListener()
        accessTokenReceiveListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recycleViewUsers.adapter = null
    }
    //endregion

    //region INITIALIZATION
    private fun initAdapter() {
        binding.recycleViewUsers.adapter = adapter
    }

    private fun initialization() {
        userViewModel.state.observe(viewLifecycleOwner) { onStateChanged(it) }
        if (adapter.itemCount == 0) {
            userViewModel.fetchUserByMostFollowed(
                requestType = UserViewModel.RequestType.LOAD
            )
        }
    }

    private fun initPullToRefresh() {
        binding.swipeContainer.setOnRefreshListener {
            userViewModel.fetchUserByMostFollowed(
                requestType = UserViewModel.RequestType.PULL_TO_REFRESH
            )
        }
    }

    private fun accessTokenReceiveListener() {

        lifecycleScope.launch {
            githubLoginViewModel.accessTokenChannel.consumeAsFlow().collect {
                val message = if (it.isEmpty())
                    getString(R.string.get_access_token_failure)
                else getString(R.string.get_access_token_success)

                // show login success/failure message to user
                UIUtils.showToast(requireContext(), message, Toast.LENGTH_LONG)

                userViewModel.getLastFailedRequestType()?.let { lastFailedRequestType ->
                    userViewModel.fetchUserByMostFollowed(lastFailedRequestType)
                }
            }
        }
    }

    private fun initLoadMoreListener() {

        binding.recycleViewUsers.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                (binding.recycleViewUsers.layoutManager as? LinearLayoutManager)?.let { layoutManager ->
                    val totalItem: Int = layoutManager.itemCount
                    val lastVisibleItem: Int = layoutManager.findLastVisibleItemPosition()
                    if (userViewModel.isDataLoading()
                            .not() && lastVisibleItem + VISIBLE_THRESHOLD >= totalItem - 1
                    ) {
                        userViewModel.setDataLoading(true)
                        binding.recycleViewUsers.post {
                            userViewModel.fetchUserByMostFollowed(
                                requestType = UserViewModel.RequestType.LOAD_MORE
                            )
                        }
                    }
                }
            }
        })

    }

    //endregion

    //region UTILS

    @Suppress("UNCHECKED_CAST")
    private fun onStateChanged(state: ViewState?) = when (state) {
        is ViewState.StateProgress -> {
            binding.userViewStates.flipper.progress()
        }
        is ViewState.DataLoaded -> if (state.requestType == UserViewModel.RequestType.LOAD_MORE) addUser(
            state.users as? List<User> ?: listOf()
        ) else setUsers(state.users as? List<User> ?: listOf())
        is ViewState.StateError -> showError(
            state.throwable
        )
        is ViewState.LimitReached -> {
            userViewModel.setLastFailedRequestType(state.requestType)
            adapter.setLimitReachedMessage(true, state.throwable.localizedMessage)
            setSwipeToRefreshAnimationEnd()
            binding.userViewStates.flipper.empty()
        }
        is ViewState.LoginUser -> {
        }
        else -> binding.userViewStates.flipper.empty()
    }

    private fun setUsers(users: List<User>) = if (users.isEmpty()) {
        adapter.setLimitReachedMessage(false, StringsExt.empty())
        binding.userViewStates.flipper.message()
        setSwipeToRefreshAnimationEnd()
        adapter.clearItems()
    } else {
        adapter.setLimitReachedMessage(false, StringsExt.empty())
        setSwipeToRefreshAnimationEnd()
        binding.userViewStates.flipper.empty()
        binding.recycleViewUsers.show()
        adapter.setItems(users)
    }

    private fun addUser(users: List<User>) {
        adapter.setLimitReachedMessage(false, StringsExt.empty())
        setSwipeToRefreshAnimationEnd()
        binding.userViewStates.flipper.empty()
        adapter.addMore(users)
    }

    private fun showError(throwable: Throwable) {
        if (adapter.itemCount > 0) {
            UIUtils.showToast(requireContext(), throwable.localizedMessage, Toast.LENGTH_LONG)
        } else {
            binding.recycleViewUsers.gone()
            binding.userViewStates.apply {
                textMessage.text = throwable.localizedMessage
                flipper.message()
            }

        }
        setSwipeToRefreshAnimationEnd()
    }

    private fun setSwipeToRefreshAnimationEnd() {
        binding.swipeContainer.isRefreshing = false
    }

    private fun showUserDetails(name: String, url: String) {

        val bundle: Bundle = bundleOf(
            UserDetailsFragment.URL_TO_BROWSE to url,
            TOP_TITLE to name.uppercase()
        )
        findNavController().navigate(
            R.id.action_userFragment_to_userDetailsFragment,
            bundle
        )
    }

    private fun loginUser() {
        userViewModel.state.value = ViewState.LoginUser
        findNavController().navigate(R.id.action_userFragment_to_githubLoginFragment)
    }

    private fun isUserAvailable() = userViewModel.isUserAvailable()

    //endregion
}