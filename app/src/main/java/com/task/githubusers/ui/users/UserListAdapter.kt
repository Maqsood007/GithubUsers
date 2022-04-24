package com.task.githubusers.ui.users

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.task.githubusers.databinding.ListItemLimitReachedBinding
import com.task.githubusers.databinding.ListItemUserBinding
import com.task.githubusers.repository.models.User
import com.task.githubusers.utils.extension.StringsExt
import com.task.githubusers.utils.extension.safeGet

/**
 * Recycler adapter for User List in [UserFragment]
 */
class UserListAdapter(
    private val onItemClick: (String, String) -> Unit,
    private val onLoginClick: () -> Unit,
    private val isUserAvailable: () -> Boolean
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = ArrayList<User>()
    private var limitReachedMessageVisibility = false
    private var limitReachedMessage = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == VIEW_TYPE_USER) {
            ViewHolder(
                ListItemUserBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else ViewHolderLogin(
            ListItemLimitReachedBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val user = items[position]
                holder.listItemUserBinding.apply {
                    root.setOnClickListener {
                        onItemClick(
                            user.login.safeGet(),
                            user.html_url.safeGet()
                        )
                    }
                    textViewUserName.text = user.login
                    textViewDescription.text = "${holder.adapterPosition}"
                    Picasso.get()
                        .load(user.avatar_url)
                        .into(imageViewUser)
                }
            }
            is ViewHolderLogin -> {
                holder.listItemLimitReachedBinding.apply {
                    textViewMessageLimitReached.text = limitReachedMessage

                    btnLogin.setOnClickListener {
                        onLoginClick.invoke()
                    }
                    btnLogin.visibility = if (isUserAvailable.invoke()) View.GONE else View.VISIBLE
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (limitReachedMessageVisibility && position == items.size) VIEW_TYPE_LIMIT_REACHED else VIEW_TYPE_USER
    }

    override fun getItemCount(): Int =
        if (limitReachedMessageVisibility) items.size + 1 else items.size

    fun setItems(users: List<User>) {
        items.clear()
        items.addAll(users)
        notifyDataChanged()
    }

    fun addMore(users: List<User>) {
        items.addAll(users)
        notifyDataChanged()
    }

    fun clearItems() {
        items.clear()
        notifyDataChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun notifyDataChanged() {
        notifyDataSetChanged()
    }

    fun setLimitReachedMessage(visible: Boolean, message: String) {
        limitReachedMessageVisibility = visible
        limitReachedMessage = if (visible) message else StringsExt.empty()
        notifyDataChanged()
    }

    class ViewHolder(val listItemUserBinding: ListItemUserBinding) :
        RecyclerView.ViewHolder(listItemUserBinding.root)

    class ViewHolderLogin(val listItemLimitReachedBinding: ListItemLimitReachedBinding) :
        RecyclerView.ViewHolder(listItemLimitReachedBinding.root)

    companion object {
        const val VIEW_TYPE_USER = 0
        const val VIEW_TYPE_LIMIT_REACHED = 1
    }
}