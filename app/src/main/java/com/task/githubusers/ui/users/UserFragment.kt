package com.task.githubusers.ui.users

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.task.githubusers.R
import com.task.githubusers.databinding.FragmentUserBinding
import com.task.githubusers.repository.models.User
import com.task.githubusers.ui.users.details.UserDetailsFragment
import com.task.githubusers.ui.users.details.UserDetailsFragment.Companion.TOP_TITLE
import com.task.githubusers.utils.extension.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserFragment : Fragment() {

    //region VARIABLES
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var binding: FragmentUserBinding
    private var adapter: UserListAdapter? = null
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
        activity?.lifecycleScope?.launchWhenCreated {
            initialization()
            initPullToRefresh()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recycleViewUsers.adapter = null
    }
    //endregion

    //region INITIALIZATION
    private fun initAdapter() {
        adapter = UserListAdapter { name, url -> showUserDetails(name, url) }
        binding.recycleViewUsers.adapter = adapter

    }

    private fun initialization() {
        userViewModel.state.observe(viewLifecycleOwner) { onStateChanged(it) }
        userViewModel.mostFollowedQuery.let {
            userViewModel.search(
                query = it,
                requestType = UserViewModel.RequestType.LOAD
            )
        }
    }

    private fun initPullToRefresh() {
        binding.swipeContainer.setOnRefreshListener {
            userViewModel.mostFollowedQuery.let {
                userViewModel.search(
                    query = it,
                    requestType = UserViewModel.RequestType.PULL_TO_REFRESH
                )
            }
        }
    }
    //endregion

    //region UTILS
    private fun onStateChanged(state: ViewState?) = when (state) {
        is ViewState.StateProgress -> binding.userViewStates.flipper.progress()
        is ViewState.DataLoaded -> setUsers(state.users as List<User>)
        is ViewState.StateError -> showError(
            state.throwable,
            R.string.error_message_search
        )
        else -> binding.userViewStates.flipper.empty()
    }

    private fun setUsers(users: List<User>) = if (users.isEmpty()) {
        binding.userViewStates.flipper.message()
        setSwipeToRefreshAnimationEnd()
        adapter?.clearItems()
    } else {
        setSwipeToRefreshAnimationEnd()
        binding.userViewStates.flipper.empty()
        adapter?.setItems(users)
    }

    private fun showError(throwable: Throwable, message: Int) {
        binding.swipeContainer.gone()
        binding.userViewStates.flipper.message()
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
    //endregion
}