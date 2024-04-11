package com.cs388.socialsync

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ChooseDateAdapter(private val context: AddEventChooseDates, private val items: List<LocalDate>): RecyclerView.Adapter<ChooseDateAdapter.ViewHolder>() {
    var onLongClick : ((LocalDate)-> Unit)? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val day: TextView
        val date: TextView

        init{
            day = itemView.findViewById(R.id.specificDate_day)
            date = itemView.findViewById(R.id.specificDate_date)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseDateAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.selected_date_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChooseDateAdapter.ViewHolder, position: Int) {
        val item = items.get(position)
        val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        Log.d("TEST", "ADDED")
        holder.date.text =  item.format(dateFormatter)
        holder.day.text = item.dayOfWeek.toString().substring(0,3).uppercase()

        holder.itemView.setOnLongClickListener{
            onLongClick?.invoke(item)
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}