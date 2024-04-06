package com.cs388.socialsync

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class UserAdapter(
    private val context: Context,
    private val users: List<User>,
    private val isAttendingList: Boolean
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private val filteredUsers: List<User> = if (isAttendingList) {
        users.filter { it.attending == "yes" }
    } else {
        users
    }
    //users.filter { it.attending != "yes" }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.user_item, parent, false))
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(filteredUsers[position])
    }

    override fun getItemCount(): Int = filteredUsers.size

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val userNameTextView: TextView = itemView.findViewById(R.id.nameUserItem)
        private val profileImageView: ImageView = itemView.findViewById(R.id.profilePic)
        private val attendingImageView: ImageView = itemView.findViewById(R.id.attending)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(user: User) {
            userNameTextView.text = user.firstName

            val attendingIconResource = when (user.attending) {
                "yes" -> R.drawable.check_icon
                "no" -> R.drawable.x_icon
                else -> R.drawable.default_icon
            }
            Glide.with(context)
                .load(attendingIconResource)
                .into(attendingImageView)
            attendingImageView.visibility = if (!isAttendingList) View.VISIBLE else View.GONE
        }

        override fun onClick(v: View?) {
            val user = filteredUsers[absoluteAdapterPosition]
            Toast.makeText(context, "You clicked on ${user.firstName}!", Toast.LENGTH_SHORT).show()
        }
    }
}
