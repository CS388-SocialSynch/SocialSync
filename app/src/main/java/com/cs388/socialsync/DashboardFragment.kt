package com.cs388.socialsync

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.children
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kizitonwose.calendar.core.*
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

class DashboardFragment : Fragment() {

    // Views
    private lateinit var timeTextView: TextView
    private lateinit var monthText: TextView
    private lateinit var amPmTextView: TextView
    private lateinit var calendarView: CalendarView
    private lateinit var eventsRecyclerView: RecyclerView
    private lateinit var joinButton: AppCompatButton
    private lateinit var roomView: EditText
    private lateinit var addEventButton: ImageView

    // Adapter
    private lateinit var eventAdapter: EventAdapter

    // Date format
    private val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    // Selected date
    private var selectedDate: LocalDate? = null

    // Event list
    private val eventList: MutableList<Event> = mutableListOf()

    // Days of week
    private val daysOfWeek = daysOfWeek()

    //var eventList: MutableList<Event> = mutableListOf()


    override fun onResume() {
        super.onResume()
        Log.e("CUTOM---->", "onResume")

        Obj.loadEvents(object : Obj.SetOnLoadEventListener {
            override fun onDataLoad() {

                Log.d("CUTOM---->", "onDataLoad")

                eventList.clear()
                eventList.addAll(Obj.eventList)
                Log.d("event list", eventList.toString())
                eventAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        initializeViews(view)
        setupTimeReceiver()
        setupCalendarView()

        val reg = Regex("^[0-9a-fA-F]{6}$")
        Obj.loggedUserID=Obj.auth.currentUser!!.uid

        joinButton.setOnClickListener {
            if(!(roomView.text.length == 6 && reg.containsMatchIn(roomView.text))){
                Toast.makeText(activity, "Invalid join code", Toast.LENGTH_SHORT).show()
            }
            else{
            // TODO start detail activity with event
            val id = roomView.text.toString().lowercase()
            hideKeyboard()
            Obj.fetchEventUsingCode(id, object : Obj.SetOnEventFetchListener {
                override fun onEventFetch(event: Event) {
                    Toast.makeText(activity, "Joined" + event.eventName, Toast.LENGTH_LONG).show()
                    // generate a new fragment and then switch
                    val fragment = EventDetail()
//                    val bundle = Bundle()
//                    bundle.putSerializable(EVENT_ITEM, event)
//                    fragment.arguments = bundle
                   Obj.event = event
                   roomView.setText("")

                    // how to switch fragments
                    (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frame_layout, fragment)
                        .addToBackStack(null)
                        .commit()
                }
            })
            }
        }

        addEventButton.setOnClickListener {
            Obj.event = Event()
            Obj.event.addressZipcode = ""
            startActivity(Intent(context, AddEventMainActivity::class.java))
        }

        return view
    }

    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(roomView.windowToken, 0)
    }

    private fun initializeViews(view: View) {
        timeTextView = view.findViewById(R.id.timeTextView)
        amPmTextView = view.findViewById(R.id.amPmTextView)
        calendarView = view.findViewById(R.id.calendarView)
        monthText = view.findViewById(R.id.monthText)
        joinButton = view.findViewById(R.id.joinButton)
        roomView = view.findViewById(R.id.roomCode)
        addEventButton = view.findViewById(R.id.addEvent)
        eventsRecyclerView = view.findViewById(R.id.upcomingEvents_recyclerView)
        eventsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        eventsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        Log.d("event list in dashboard", eventList.toString())
        eventAdapter = EventAdapter(requireContext(), eventList)
        eventsRecyclerView.adapter = eventAdapter

        //Log.e("CUSTOM0000>", Obj.user.events.toString())
    }

    private fun setupTimeReceiver() {
        updateTime()
        val intentFilter = IntentFilter(Intent.ACTION_TIME_TICK)
        activity?.registerReceiver(timeTickReceiver, intentFilter)
    }

    private val timeTickReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_TIME_TICK) {
                updateTime()
            }
        }
    }

    private fun updateTime() {
        val currentTime = dateFormat.format(Date())
        val timeParts = currentTime.split(" ")
        timeTextView.text = timeParts[0]
        "${timeParts[1][0]}\n${timeParts[1][1]}".also { amPmTextView.text = it }
        amPmTextView.setLineSpacing(0f, 0.8f)
    }

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
                    Toast.makeText(requireContext(), selectedDate.toString(), Toast.LENGTH_SHORT)
                        .show()
                    currentSelection?.let {
                        calendarView.notifyDateChanged(it)
                    }
                }
            }
            Toast.makeText(requireContext(), selectedDate.toString(), Toast.LENGTH_SHORT).show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.unregisterReceiver(timeTickReceiver)
    }

    companion object {
        fun newInstance(): DashboardFragment {
            return DashboardFragment()
        }
    }
}
