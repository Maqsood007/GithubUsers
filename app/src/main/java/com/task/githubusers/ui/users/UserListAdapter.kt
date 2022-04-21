package com.task.githubusers.ui.users

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.task.githubusers.R
import com.task.githubusers.databinding.ListItemUserBinding
import com.task.githubusers.repository.models.User
import com.task.githubusers.utils.extension.safeGet

/**
 * Recycler adapter for User List in [UserFragment]
 */
class UserListAdapter(private val onItemClick: (String, String) -> Unit) :
    RecyclerView.Adapter<UserListAdapter.ViewHolder>() {

    private val items = ArrayList<User>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemUserBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = items[position]
        holder.listItemUserBinding.apply {
            root.setOnClickListener { onItemClick(user.login.safeGet(), user.html_url.safeGet()) }
            textViewUserName.text = user.login
            Picasso.get()
                .load(user.avatar_url)
                .into(imageViewUser)
        }
    }

    override fun getItemCount(): Int = items.size

    fun setItems(users: List<User>) {
        items.clear()
        items.addAll(users)
        notifyDataSetChanged()
    }

    fun clearItems() {
        items.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(val listItemUserBinding: ListItemUserBinding) :
        RecyclerView.ViewHolder(listItemUserBinding.root)

}