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

    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var binding: FragmentUserBinding

    private val adapter = UserListAdapter { name, url -> showUserDetails(name, url) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recycleViewUsers.adapter = adapter
        activity?.lifecycleScope?.launchWhenCreated {
            initialization(savedInstanceState)
        }
    }

    private fun initialization(savedInstanceState: Bundle?) {
        userViewModel.state.observe(viewLifecycleOwner) { onStateChanged(it) }
        "followers:>=0&sort:followers".let { userViewModel.search(it) }
    }

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
        adapter.clearItems()
    } else {
        binding.userViewStates.flipper.empty()
        adapter.setItems(users)
    }

    private fun showError(throwable: Throwable, message: Int) {
        binding.userViewStates.flipper.message()
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
}