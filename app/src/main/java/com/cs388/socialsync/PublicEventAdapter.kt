package com.cs388.socialsync

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PublicEventAdapter(private val publicEventList: List<PublicEvent>) :
    RecyclerView.Adapter<PublicEventAdapter.PublicEventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublicEventViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.event_item, parent, false)
        return PublicEventViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PublicEventViewHolder, position: Int) {
        val currentItem = publicEventList[position]

        holder.eventNameTextView.text = currentItem.eventName
        "${currentItem.startTime} - ${currentItem.endTime}".also { holder.timeTextView.text = it }
        holder.dateTextView.text = currentItem.date
        "${currentItem.temperature}°F".also { holder.temperatureTextView.text = it }

        val weatherIconResource = when (currentItem.weatherCondition) {
            "cloudy" -> R.drawable.cloudy_icon
            "sunny" -> R.drawable.sunny_icon
            else -> R.drawable.default_icon
        }

        Glide.with(holder.itemView.context)
            .load(weatherIconResource)
            .into(holder.weatherImageView)
    }

    override fun getItemCount() = publicEventList.size

    inner class PublicEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val eventNameTextView: TextView = itemView.findViewById(R.id.eventNameTextView)
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val temperatureTextView: TextView = itemView.findViewById(R.id.temperatureTextView)
        val weatherImageView: ImageView = itemView.findViewById(R.id.imageView)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val publicEvent = publicEventList[absoluteAdapterPosition]
            val eventName = publicEvent.eventName
            val toastMessage = "Public Event clicked: $eventName"
            Toast.makeText(itemView.context, toastMessage, Toast.LENGTH_SHORT).show()
        }
    }
}
