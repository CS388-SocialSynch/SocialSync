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
    private val users: ArrayList<User>
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.user_item, parent, false))
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val userNameTextView: TextView = itemView.findViewById(R.id.nameUserItem)
        private val profileImageView: ImageView = itemView.findViewById(R.id.profilePic)
        private val attendingImageView: ImageView = itemView.findViewById(R.id.attending)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(user: User) {
            userNameTextView.text = user.name

            val attendingIconResource = when (user.attending) {
                true -> R.drawable.check_icon
                false -> R.drawable.x_icon
            }
            Glide.with(context)
                .load(attendingIconResource)
                .into(attendingImageView)
        }

        override fun onClick(v: View?) {
            val user = users[absoluteAdapterPosition]
            Toast.makeText(context, "You clicked on ${user.name}!", Toast.LENGTH_SHORT).show()
        }
    }
}
