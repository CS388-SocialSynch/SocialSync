package com.cs388.socialsync

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class TimeslotAdapter(
    private val context: Context,
    private val startTime: String,
    private val endTime: String,
    private val listener: OnTimeslotSelectionListener
) : RecyclerView.Adapter<TimeslotAdapter.TimeslotViewHolder>()  {

    private val timeslots: List<String> = genTimeslots(startTime, endTime)
    private val lastClickedPositions = mutableListOf<Int>()
    override fun onBindViewHolder(holder: TimeslotAdapter.TimeslotViewHolder, position: Int) {
        holder.bind(timeslots[position],position)
    }

    override fun getItemCount(): Int = timeslots.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeslotAdapter.TimeslotViewHolder {
        return TimeslotViewHolder(
            LayoutInflater.from(parent.context)
            .inflate(R.layout.timeslot_item, parent, false))
    }


    inner class TimeslotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        private val timeslotButton: Button = itemView.findViewById(R.id.timeslotButton)
        init {
           timeslotButton.setOnClickListener {

               val position = absoluteAdapterPosition
               val time = timeslots[position]
               //Toast.makeText(itemView.context, "$time : ${lastClickedPositions.joinToString()}", Toast.LENGTH_SHORT).show()

               if (lastClickedPositions.contains(position)) {
                   lastClickedPositions.remove(position)
               } else {
                   if (lastClickedPositions.size >= 2) {
                       lastClickedPositions.removeAt(0)
                   }
                   lastClickedPositions.add(position)

               }

               // Notify the adapter to refresh items. This is a simple but not the most efficient way.
               notifyDataSetChanged()
               updateSelectedCount()

           }

        }
        fun bind(time: String, position: Int) {
            timeslotButton.text = time

            if (lastClickedPositions.contains(position)) {
                timeslotButton.background = ContextCompat.getDrawable(itemView.context, R.drawable.black_stroke)
            } else {
                // Revert to default background
                timeslotButton.background =  ContextCompat.getDrawable(itemView.context, R.drawable.button_stroke)
            }

            if (lastClickedPositions.size >= 2){

            }
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

    private fun updateSelectedCount() {
        val times =  mutableListOf<String>()
        for(pos in lastClickedPositions){
            times.add(timeslots[pos])
        }
        listener.onTimeslotsSelected(times)
    }
}