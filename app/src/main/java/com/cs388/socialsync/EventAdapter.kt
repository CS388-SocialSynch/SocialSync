package com.cs388.socialsync

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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
        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
        val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        var startStr = "null"
        var endStr = "null"
        if (currentItem.startTime != null && currentItem.startTime != "null") {
            startStr = LocalTime.parse(currentItem.startTime, DateTimeFormatter.ISO_LOCAL_TIME)
                .format(timeFormatter)
        }
        if (currentItem.endTime != null && currentItem.endTime != "null") {
            endStr = LocalTime.parse(currentItem.endTime, DateTimeFormatter.ISO_LOCAL_TIME)
                .format(timeFormatter)
        }

        if (currentItem.endTime != null) {

            holder.eventNameTextView.text = currentItem.eventName
            "${startStr} - ${endStr}".also { holder.timeTextView.text = it }
        } else {
            holder.eventNameTextView.text = currentItem.eventName
            startStr.also {
                holder.timeTextView.text = it
            }
        }

        var dateStr = "null"
        if (currentItem.date != "" && currentItem.date != null) {
            dateStr = LocalDate.parse(currentItem.date, DateTimeFormatter.ISO_LOCAL_DATE)
                .format(dateFormatter)
        }
        holder.dateTextView.text = dateStr
        //"${currentItem.temperature}°F".also { holder.temperatureTextView.text = it }

        val weatherFetcher = WeatherFetcher()

        val zipCode = currentItem.address
        Log.d("zipcode", zipCode)
        // Call fetchWeather method to fetch weather data
        weatherFetcher.fetchWeather(zipCode) { weatherCondition, temperature, humidity, windSpeed, feelLike ->
            // Update UI with fetched weather data
            val weatherIconResource = when (weatherCondition) {
                "Clear" -> R.drawable.sunny_icon
                "Clouds", "Mist", "Haze", "Fog" -> R.drawable.cloudy_icon
                "Rain", "Drizzle" -> R.drawable.rainy_icon
                "Thunderstorm" -> R.drawable.stormy_icon
                "Snow" -> R.drawable.snowy_icon
                else -> R.drawable.default_icon
            }
            Log.d("humidity", humidity.toString())
            Log.d("wind", windSpeed.toString())
            Log.d("feelsLike", feelLike.toString())
            currentItem.temperature = temperature
            currentItem.weatherCondition = weatherCondition
            currentItem.windSpeed = windSpeed
            currentItem.humidity = humidity
            currentItem.feelLike = feelLike
            // Load weather image using Glide on the main thread
            (holder.itemView.context as AppCompatActivity).runOnUiThread {
                Glide.with(holder.itemView.context)
                    .load(weatherIconResource)
                    .into(holder.weatherImageView)

                // Set temperature text on the main thread
                "${temperature}°F".also { holder.temperatureTextView.text = it }
            }
        }
    }

    override fun getItemCount() = eventList.size

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
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
            // generate a new fragment and then switch
            val fragment = EventDetail()
            val bundle = Bundle()
            bundle.putSerializable(EVENT_ITEM, event)
            fragment.arguments = bundle

            // how to switch fragments
            (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frame_layout, fragment)
                .addToBackStack(null)
                .commit()
        }

    }
}
