package com.cs388.socialsync.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cs388.socialsync.R
import com.cs388.socialsync.model.EventModel

class EventAdapter(val mList: ArrayList<EventModel>, val lister: OnItemDeleteClickListener) :
    RecyclerView.Adapter<EventAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_design, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]!!


        holder.tvEventName.text = ItemsViewModel.eventName
        holder.tvAddress.text = ItemsViewModel.address

        holder.ivDelete.setOnClickListener {
            lister.onItemDeleteClick(ItemsViewModel.key)
        }

    }

    interface OnItemDeleteClickListener {
        fun onItemDeleteClick(key: String)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val ivDelete: ImageView = itemView.findViewById(R.id.ivDelete)
        val tvEventName: TextView = itemView.findViewById(R.id.tvEventName)
        val tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
    }
}