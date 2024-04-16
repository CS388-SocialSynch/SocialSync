package com.cs388.socialsync


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class DateAdapter(
    private val context: Context,
    private val dates: List<String>,
    private val listener: OnDateSelectionListener,
    private val showDates: Boolean
) : RecyclerView.Adapter<DateAdapter.DateViewHolder>()  {

    private var selectedPosition = RecyclerView.NO_POSITION
    override fun onBindViewHolder(holder: DateAdapter.DateViewHolder, position: Int) {
        holder.bind(dates[position],position==selectedPosition)
    }


    override fun getItemCount(): Int = dates.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateAdapter.DateViewHolder {
        return DateViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.date_item, parent, false))
    }


    inner class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val dateTV: TextView = itemView.findViewById(R.id.date_item_date)
        private val dayTV: TextView = itemView.findViewById(R.id.date_item_dayAbv)
        private val underline: View = itemView.findViewById(R.id.date_item_underline)
        private val dateButton: LinearLayout = itemView.findViewById(R.id.dateButton)

        init {
            itemView.setOnClickListener(this)


        }
        fun bind(date: String, isSelected:Boolean) {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val outputFormatter = DateTimeFormatter.ofPattern("MM/dd")
            val currDate = LocalDate.parse(date, inputFormatter)
            dayTV.text = currDate.dayOfWeek.toString().substring(0, 3)
            if(showDates)
                dateTV.text =  currDate.format(outputFormatter)
            if (isSelected){
                dateButton.background=AppCompatResources.getDrawable(itemView.context,R.drawable.selected_date)
            }else{
                dateButton.background=AppCompatResources.getDrawable(itemView.context,R.drawable.unselected_date)
            }
            underline.visibility =  if (isSelected) View.VISIBLE else View.GONE
        }

        override fun onClick(v: View?) {
            val newPosition = bindingAdapterPosition
            if(newPosition != RecyclerView.NO_POSITION) {
                val oldPosition = selectedPosition
                selectedPosition = newPosition
                notifyItemChanged(oldPosition)
                notifyItemChanged(newPosition)
                updateSelectedCount()
                //Toast.makeText(context, "You clicked on ${dates[position]}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun updateSelectedCount() {
        listener.onDateSelected(dates[selectedPosition])
    }
}