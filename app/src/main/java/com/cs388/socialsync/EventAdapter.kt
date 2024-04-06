package com.cs388.socialsync

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

const val EVENT_ITEM = "EVENT_ITEM"

class EventAdapter(private val context: Context, private val eventList: List<Event>) :
    RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.event_item, parent, false)
        return EventViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val currentItem = eventList[position]

        holder.eventNameTextView.text = currentItem.eventName
        "${currentItem.startTime} - ${currentItem.endTime}".also { holder.timeTextView.text = it }
        holder.dateTextView.text = currentItem.date
        "${currentItem.temperature}°F".also { holder.temperatureTextView.text = it }

        // Load weather image using Glide
        val weatherIconResource = when (currentItem.weatherCondition) {
            "cloudy" -> R.drawable.cloudy_icon
            "sunny" -> R.drawable.sunny_icon
            else -> R.drawable.default_icon
        }

        Glide.with(holder.itemView.context)
            .load(weatherIconResource)
            .into(holder.weatherImageView)
    }

    override fun getItemCount() = eventList.size

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val eventNameTextView: TextView = itemView.findViewById(R.id.eventNameTextView)
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val temperatureTextView: TextView = itemView.findViewById(R.id.temperatureTextView)
        val weatherImageView: ImageView = itemView.findViewById(R.id.imageView)

        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val event = eventList[absoluteAdapterPosition]

            val fragment = EventDetail()
            val bundle = Bundle()
            bundle.putSerializable(EVENT_ITEM, event)
            fragment.arguments = bundle

            (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frame_layout, fragment)
                .addToBackStack(null)
                .commit()
        }

    }
}

