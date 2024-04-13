package com.cs388.socialsync

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.cs388.socialsync.databinding.FragmentEventsBinding
import java.time.LocalDate
import java.time.LocalTime
import kotlin.random.Random

class EventsFragment : Fragment() {


    private lateinit var binding: FragmentEventsBinding
    private lateinit var publicEventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventsBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.publicEventsRecyclerView.layoutManager = LinearLayoutManager(activity)

        val events: List<Event> = createRandomEvents()

        publicEventAdapter = EventAdapter(requireContext(), events)
        binding.publicEventsRecyclerView.adapter = publicEventAdapter

        return view
    }

    private fun createRandomEvents(): List<Event> {
        val events = mutableListOf<Event>()
        val eventNames = mutableListOf<String>()

        // Sample event names
        val sampleNames = listOf(
            "Birthday Bash",
            "Concert Night",
            "Tech Meetup",
            "Food Festival",
            "Art Exhibition",
            "Networking"
        )

        repeat(6) {
            var eventName = ""
            do {
                eventName = sampleNames.random()
            } while (eventName in eventNames)

            eventNames.add(eventName)

            val startTime = LocalTime.of(10,0)//"10 AM"
            val endTime = LocalTime.of(12,0)//"12 PM"
            val date = LocalDate.of(2024,4,9)//"04/09/2024"
            val temperature = Random.nextInt(50, 100)
            val weatherCondition = if (it % 2 == 0) "cloudy" else "sunny"
            val location = "NJIT"
            val address = "CC"

            val event = Event(
                "what",
                eventName,
                startTime,
                endTime,
                date,
                temperature,
                weatherCondition,
                location,
                address,
                false,
                true
            )
            events.add(event)
        }
        return events
    }
}
