package com.cs388.socialsync

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.children
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

        val btnFinish = findViewById<AppCompatButton>(R.id.btnFinish)
        val btnBack = findViewById<AppCompatButton>(R.id.btnBack)

        val event = intent.getBundleExtra("eventInfo")?.getSerializable(EVENT_ITEM) as? Event

        adapter.onLongClick = {
            eventList.remove(it)
            adapter.notifyDataSetChanged()
        }

        btnFinish.setOnClickListener(){
            event?.useSpecificDate=true
            event?.optionalDates?.clear()
//            event?.optionalDates?.addAll(eventList)

            // Send info back to the previous activity
            val launchNextActivity: Intent = Intent(
                this@AddEventChooseDates,
                AddEventSelectDate::class.java
            )
            launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            val bundle = Bundle()
            bundle.putSerializable(EVENT_ITEM, event)
            launchNextActivity.putExtra("eventInfo",bundle)
            startActivity(launchNextActivity)
            finish()
        }
        btnBack.setOnClickListener(){
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder
                .setTitle("Discard Choices?")
                .setMessage("Are you sure you want to discard the choosen dates? (This will not discard new event work)")
                .setPositiveButton("Discard") { dialog, which ->
                    finish()
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, which ->
                    dialog.cancel()
                }

            val dialog: AlertDialog = builder.create()
            dialog.show()
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
                    eventList.sort()
                    adapter.notifyDataSetChanged()
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