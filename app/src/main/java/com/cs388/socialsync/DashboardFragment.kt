package com.cs388.socialsync

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DashboardFragment : Fragment() {

    private lateinit var timeTextView: TextView
    private lateinit var amPmTextView: TextView
    private lateinit var calendarView: CalendarView
    private lateinit var joinButton : Button
    private lateinit var roomView : EditText
    private lateinit var addEventButton : ImageView
    private val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        timeTextView = view.findViewById(R.id.timeTextView)
        amPmTextView = view.findViewById(R.id.amPmTextView)
        calendarView = view.findViewById(R.id.calendarView)
        joinButton = view.findViewById(R.id.joinButton)
        roomView = view.findViewById(R.id.roomCode)
        addEventButton = view.findViewById(R.id.addEvent)

        updateTime()
        registerTimeReceiver()

        val currentDate = Calendar.getInstance()
        calendarView.minDate = currentDate.timeInMillis

        calendarView.setOnDateChangeListener { calendarView, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)

            if (selectedDate.before(currentDate)) {
                // Reset CalendarView to the current date
                calendarView.date = currentDate.timeInMillis
            }
        }

        joinButton.setOnClickListener {
            Toast.makeText(requireContext(), roomView.text, Toast.LENGTH_SHORT).show()
        }

        addEventButton.setOnClickListener {
            Toast.makeText(requireContext(), "You are now breathing manually :)", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun updateTime() {
        val currentTime = dateFormat.format(Date())
        val timeParts = currentTime.split(" ")
        timeTextView.text = timeParts[0]
        "${timeParts[1][0]}\n${timeParts[1][1]}".also { amPmTextView.text = it }
        amPmTextView.setLineSpacing(0f, 0.8f)
    }

    private fun registerTimeReceiver() {
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

