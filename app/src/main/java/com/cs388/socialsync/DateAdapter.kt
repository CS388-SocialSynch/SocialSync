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
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class DateAdapter(
    private val context: Context,
    private val startDate: String,
    private val endDate: String
) : RecyclerView.Adapter<DateAdapter.DateViewHolder>()  {

    private val dates: List<String> = genDates(startDate, endDate)
    override fun onBindViewHolder(holder: DateAdapter.DateViewHolder, position: Int) {
        holder.bind(dates[position])
    }


    override fun getItemCount(): Int = dates.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateAdapter.DateViewHolder {
        return DateViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.date_item, parent, false))
    }


    inner class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val dateButton: TextView = itemView.findViewById(R.id.dateButton)
        init {
            itemView.setOnClickListener(this)
        }
        fun bind(date: String) {
            dateButton.text = date
        }

        override fun onClick(v: View?) {
            val date = dates[absoluteAdapterPosition]
            Toast.makeText(context, "You clicked on ${date}!", Toast.LENGTH_SHORT).show()
        }
    }
    private fun genDates(startDate: String, endDate: String): List<String> {
        val dateList = mutableListOf<String>()

        // Parse the start and end dates
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        var currentDate = LocalDate.parse(startDate, formatter)
        val finalDate = LocalDate.parse(endDate, formatter)

        if (finalDate.isBefore(currentDate)) {
            return dateList
        }

        while (!currentDate.isAfter(finalDate)) {
            dateList.add(currentDate.format(formatter))
            currentDate = currentDate.plusDays(1)
        }

        return dateList
    }
}