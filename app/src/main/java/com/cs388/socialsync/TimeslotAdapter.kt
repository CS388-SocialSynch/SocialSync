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
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class TimeslotAdapter(
    private val context: Context,
    private val startTime: String,
    private val endTime: String
) : RecyclerView.Adapter<TimeslotAdapter.TimeslotViewHolder>()  {

    private val timeslots: List<String> = genTimeslots(startTime, endTime)
    override fun onBindViewHolder(holder: TimeslotAdapter.TimeslotViewHolder, position: Int) {
        holder.bind(timeslots[position])
    }

    override fun getItemCount(): Int = timeslots.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeslotAdapter.TimeslotViewHolder {
        return TimeslotViewHolder(
            LayoutInflater.from(parent.context)
            .inflate(R.layout.timeslot_item, parent, false))
    }


    inner class TimeslotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val timeslotButton: TextView = itemView.findViewById(R.id.timeslotButton)
        init {
            itemView.setOnClickListener(this)
        }
        fun bind(time: String) {
            timeslotButton.text = time
        }

        override fun onClick(v: View?) {
            val time = timeslots[absoluteAdapterPosition]
            Toast.makeText(context, "You clicked on ${time}!", Toast.LENGTH_SHORT).show()
        }
    }
    private fun genTimeslots(startTime: String, endTime: String): List<String> {
        val timeList = mutableListOf<String>()
        val formatter = DateTimeFormatter.ofPattern("h:mm a")
        var currentTime = LocalTime.parse(startTime.uppercase(), formatter)
        val finalTime = LocalTime.parse(endTime.uppercase(), formatter)

        if (finalTime.isBefore(currentTime)) {
            return timeList
        }

        while (currentTime.isBefore(finalTime) || currentTime.equals(finalTime)) {
            timeList.add(currentTime.format(formatter))
            currentTime = currentTime.plus(15, ChronoUnit.MINUTES)
        }

        return timeList
    }
}