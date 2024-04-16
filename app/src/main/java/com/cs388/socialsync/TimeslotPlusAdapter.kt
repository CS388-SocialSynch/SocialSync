package com.cs388.socialsync

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


class TimeslotPlusAdapter(
    private val context: Context,
    private val startTime: String,
    private val endTime: String,
    private val listener: OnTimeslotSelectionListener
) : RecyclerView.Adapter<TimeslotPlusAdapter.TimeslotViewHolder>()  {

    data class Timeslot(val time: String, var isSelected: Boolean)

    private val timeslots: List<Timeslot> = genTimeslots(startTime, endTime).map{Timeslot(it, isSelected = false)}

    override fun onBindViewHolder(holder: TimeslotPlusAdapter.TimeslotViewHolder, position: Int) {
        holder.bind(timeslots[position],position)
    }

    override fun getItemCount(): Int = timeslots.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeslotPlusAdapter.TimeslotViewHolder {
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
                time.isSelected = !time.isSelected

                // Notify the adapter to refresh items. This is a simple but not the most efficient way.
                notifyItemChanged(position)
                updateSelectedCount()

            }

        }
        fun bind(timeslot: Timeslot, position: Int) {
            timeslotButton.text = timeslot.time

            if (timeslot.isSelected) {
                timeslotButton.background = ContextCompat.getDrawable(itemView.context, R.drawable.button_timeslot_selected)
                timeslotButton.alpha = 1f
            } else {
                // Revert to default background
                timeslotButton.background =  ContextCompat.getDrawable(itemView.context, R.drawable.button_timeslot_unselected)
                timeslotButton.alpha= 0.5f
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
        val times: List<String> = timeslots
            .filter { it.isSelected }
            .map { it.time }
        listener.onTimeslotsSelected(times)
    }
}