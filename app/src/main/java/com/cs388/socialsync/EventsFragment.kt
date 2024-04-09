package com.cs388.socialsync

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import androidx.recyclerview.widget.LinearLayoutManager
import com.cs388.socialsync.databinding.FragmentEventsBinding
import kotlin.random.Random

class EventsFragment : Fragment() {

    private lateinit var binding: FragmentEventsBinding
    private lateinit var publicEventAdapter: PublicEventAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventsBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.publicEventsRecyclerView.layoutManager = LinearLayoutManager(activity)

        val events: List<Event> = createRandomEvents()

        val listener = object : PublicEventAdapter.OnItemClickListener {
            override fun onItemClick(event: Event) {

                val intent = Intent(activity,PublicEventDetailActivity::class.java)
                intent.putExtra("event",event)
                startActivity(intent)

            }
        }

        publicEventAdapter = PublicEventAdapter(events, listener)
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

            val startTime = "10 AM"
            val endTime = "12 PM"
            val date = "04/09/2024"
            val temperature = Random.nextInt(50, 100)
            val weatherCondition = if (it % 2 == 0) "cloudy" else "sunny"
            val location = "NJIT"
            val address = "CC"

            val event = Event(
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
