package com.cs388.socialsync

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

class AddEventChooseDates: AppCompatActivity() {

    private lateinit var monthText: TextView
    private lateinit var calendarView: CalendarView
    // Selected date
    private var selectedDate: LocalDate? = null
    // Event list
    private val eventList: MutableList<LocalDate> = mutableListOf()
        // Days of week
    private val daysOfWeek = daysOfWeek()
    //RV
    private lateinit var rv: RecyclerView
    private lateinit var adapter: ChooseDateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_event_choosing_dates)

        calendarView = findViewById(R.id.calendarView)
        monthText = findViewById(R.id.monthText)


        setupCalendarView()


        rv = findViewById<RecyclerView>(R.id.specificDates)
        adapter = ChooseDateAdapter(this, eventList)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(this)

        val event = intent.getBundleExtra("eventInfo")?.getSerializable(EVENT_ITEM) as? Event
        Log.d("Choose", event.toString())

        adapter.onLongClick = {
            eventList.remove(it)
            adapter.notifyDataSetChanged()
        }

    }




    // Calender Items
    private fun setupCalendarView() {
        calendarView.monthScrollListener = { month ->
            val monthName =
                month.yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
            val year = month.yearMonth.year
            "$monthName $year".also { monthText.text = it }
        }

        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(1)
        val endMonth = currentMonth.plusMonths(100)
        calendarView.setup(startMonth, endMonth, daysOfWeek.first())
        calendarView.scrollToMonth(currentMonth)

        setupDayView()
        setupMonthHeaderView()
    }

    private fun setupDayView() {
        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.bindData(data)
            }
        }
    }

    private fun setupMonthHeaderView() {
        calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                container.bindData(data)
            }
        }
    }

    private inner class DayViewContainer(view: View) : ViewContainer(view) {
        private val textView = view.findViewById<TextView>(R.id.calendarDayText)
        private lateinit var day: CalendarDay

        init {
            view.setOnClickListener {
                handleDayClick()
            }
        }

        fun bindData(data: CalendarDay) {
            day = data
            textView.text = data.date.dayOfMonth.toString()
            updateDayView(data)
        }

        private fun handleDayClick() {
            if (day.position == DayPosition.MonthDate && !day.date.isBefore(LocalDate.now())) {
                val currentSelection = selectedDate
                if (currentSelection != day.date) {
                    selectedDate = day.date
                    calendarView.notifyDateChanged(day.date)
                    currentSelection?.let {
                        calendarView.notifyDateChanged(it)

                    }
                    if(!eventList.contains(selectedDate))
                        eventList.add(selectedDate!!)
                    adapter.notifyDataSetChanged()
                    Log.d("TEMP", eventList.toString())
                }
            }
        }

        private fun updateDayView(data: CalendarDay) {
            val today = LocalDate.now()
            val isPastDay = data.date.isBefore(today)

            if (data.position == DayPosition.MonthDate) {
                textView.visibility = View.VISIBLE
                if (data.date == selectedDate) {
                    textView.setTextColor(Color.WHITE)
                    textView.setBackgroundResource(R.drawable.selection_background)
                } else {
                    if (isPastDay) {
                        textView.setTextColor(Color.GRAY)
                    } else {
                        textView.setTextColor(Color.BLACK)
                    }
                    textView.background = null
                }
            } else {
                textView.visibility = View.INVISIBLE
            }
        }
    }

    private inner class MonthViewContainer(view: View) : ViewContainer(view) {
        val titlesContainer = view as ViewGroup

        fun bindData(data: CalendarMonth) {
            if (titlesContainer.tag == null) {
                titlesContainer.tag = data.yearMonth
                titlesContainer.children.map { it as TextView }.forEachIndexed { index, textView ->
                    val dayOfWeek = daysOfWeek[index]
                    val title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                    textView.text = title
                }
            }
        }
    }


}
//TODO
// add a back button --> discards current work for specific days and goes back to last event --> dont finish chain
// click done then go back to part 2 for final steps