package com.cs388.socialsync

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.switchmaterial.SwitchMaterial
import java.time.format.DateTimeFormatter

class EventDetail : Fragment() {

    // Views
    private lateinit var eventDetailView: TextView
    private lateinit var timeDetailView: TextView
    private lateinit var dateDetailView: TextView
    private lateinit var locationDetailView: TextView
    private lateinit var addressDetailView: TextView
    private lateinit var weatherIconDetailView: ImageView
    private lateinit var feelLikeTextView: TextView
    private lateinit var humidityTextView: TextView
    private lateinit var windTextView: TextView
    private lateinit var modifyTimeButton: AppCompatButton
    private lateinit var leaveButton: AppCompatButton
    private lateinit var modifyEventButton: AppCompatButton
    private lateinit var shareEventButton: ImageView
    private lateinit var incomingTextView: TextView
    private lateinit var attendSwitch : SwitchMaterial
    private lateinit var attendingLayout : LinearLayout
    private lateinit var incomingRecyclerView: RecyclerView

    // Adapters
    private lateinit var userAdapterIncoming: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.event_detail, container, false)

        // Initialize views
        initViews(view)

        // Get event details
        val event = arguments?.getSerializable(EVENT_ITEM) as? Event

        // Handle event visibility based on type and ownership
        handleEventVisibility(event)

        // Initialize adapters
        initAdapters()

        // Set listeners
        setListeners()

        // Display event details
        displayEventDetails(event)

        return view
    }

    private fun initViews(view: View) {
        eventDetailView = view.findViewById(R.id.eventDetailEvent)
        timeDetailView = view.findViewById(R.id.timeDetailEvent)
        dateDetailView = view.findViewById(R.id.dateDetailEvent)
        locationDetailView = view.findViewById(R.id.locationNameDetailEvent)
        addressDetailView = view.findViewById(R.id.addressDetailEvent)
        weatherIconDetailView = view.findViewById(R.id.weatherIconEventDetail)
        feelLikeTextView = view.findViewById(R.id.feelLike)
        humidityTextView = view.findViewById(R.id.humidity)
        windTextView = view.findViewById(R.id.wind)
        modifyTimeButton = view.findViewById(R.id.modifyTimeButton)
        leaveButton = view.findViewById(R.id.leaveButton)
        modifyEventButton = view.findViewById(R.id.modifyEventButton)
        shareEventButton = view.findViewById(R.id.shareEvent)
        incomingTextView = view.findViewById(R.id.incoming)
        attendSwitch = view.findViewById(R.id.attendSwitch)
        attendingLayout = view.findViewById(R.id.attendingLayout)
        incomingRecyclerView = view.findViewById(R.id.IncomingRecycler)
    }

    private fun handleEventVisibility(event: Event?) {
        event?.let { details ->
            val isPublicEvent = details.isPublic
            val isHostOfEvent = details.isHost

            if (isPublicEvent) {
                // Hide views for public events
                val viewsToHide = listOf(
                    modifyTimeButton,
                    leaveButton,
                    modifyEventButton,
                    shareEventButton,
                    incomingRecyclerView,
                    incomingTextView
                )
                viewsToHide.forEach { it.visibility = View.GONE }
            } else {
                // Show or hide buttons based on event ownership
                modifyEventButton.visibility = if (isHostOfEvent) View.VISIBLE else View.GONE
                leaveButton.visibility = if (isHostOfEvent) View.GONE else View.VISIBLE
            }
        }
    }

    private fun initAdapters() {
        val userList = listOf(
            User("Miquel", "yes"),
            User("Saketh", "no"),
            User("Ethan", "Maybe"),
            User("Karam", "yes")
        )

        userAdapterIncoming = UserAdapter(requireContext(), userList)

        incomingRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userAdapterIncoming
        }
    }

    private fun setListeners() {

        attendSwitch.setOnClickListener {
            Toast.makeText(context, attendSwitch.isChecked.toString(), Toast.LENGTH_SHORT).show()
        }

        modifyTimeButton.setOnClickListener {
            Toast.makeText(context, "You clicked on modify time", Toast.LENGTH_SHORT).show()
        }

        leaveButton.setOnClickListener {
            Toast.makeText(context, "Why leave :(", Toast.LENGTH_SHORT).show()
        }

        modifyEventButton.setOnClickListener {
            Toast.makeText(context, "You clicked on modify event", Toast.LENGTH_SHORT).show()
        }

        shareEventButton.setOnClickListener {
            Toast.makeText(context, "There is nothing to share >:(", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayEventDetails(event: Event?) {

        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

        event?.let { details ->
            eventDetailView.text = details.eventName
            "${details.startTime.format(timeFormatter)} - ${details.endTime.format(timeFormatter)}".also { timeDetailView.text = it }
            dateDetailView.text = details.date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
            locationDetailView.text = details.locationName
            addressDetailView.text = details.address

            // Load weather icon using Glide
            val weatherIconResId = when (details.weatherCondition) {
                "cloudy" -> R.drawable.cloudy_icon
                "sunny" -> R.drawable.sunny_icon
                else -> R.drawable.default_icon
            }
            Glide.with(this).load(weatherIconResId).into(weatherIconDetailView)
        }
    }
}
