package com.cs388.socialsync

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
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
    private lateinit var attendSwitch: SwitchMaterial
    private lateinit var attendingLayout: LinearLayout
    private lateinit var incomingRecyclerView: RecyclerView

    // Adapters
    private lateinit var userAdapterIncoming: UserAdapter

    // Users
    private var users: MutableList<User> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.event_detail, container, false)
        // Get event details
        val event = Obj.event
        val isCurrentUserAttending = event?.participants?.contains(Obj.loggedUserID)

        // Initialize views
        initViews(view)
        if (isCurrentUserAttending != null) {
            attendSwitch.isChecked = isCurrentUserAttending
        }


        Obj.addEventToDatabase(event!!, object : Obj.SetOnDuplicateEventCheckListener {
            override fun onDuplicateEvent() {
                // Prevent the duplicate message from popping up on every open
//                Toast.makeText(activity, "duplicate", Toast.LENGTH_SHORT).show()
            }

            override fun onEventAdded(key: String) {
                Toast.makeText(activity, "Event Added", Toast.LENGTH_SHORT).show()
            }
        }, true)

        // Handle event visibility based on type and ownership
        handleEventVisibility(event)

        // Initialize adapters and set listeners
        if (event != null) {
            if (!event.isPublic) {
                initAdapters(event)
            }
            setListeners(event)
        }
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
        modifyEventButton = view.findViewById(R.id.modifyEventButton)
    }

    private fun handleEventVisibility(event: Event?) {
        event?.let { details ->
            val isPublicEvent = details.isPublic
            val isHostOfEvent = (details.hostUID == Obj.auth.currentUser!!.uid)

            if (isPublicEvent) {
                // Hide views for public events
                val viewsToHide = listOf(
                    attendingLayout,
                    modifyTimeButton,
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

    private fun initAdapters(event: Event) {
        var fetchCount = 0

        if (event.joined.isEmpty()) {
            userAdapterIncoming = UserAdapter(requireContext(), users)
            incomingRecyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = userAdapterIncoming
            }
        } else {
            event.joined.forEach { uid ->
                Obj.fetchParticipantData(uid) { userName ->
                    val isAttending = event.participants.contains(uid)
                    val user = User(userName, isAttending)
                    users.add(user)

                    fetchCount++

                    if (fetchCount == event.joined.size) {
                        userAdapterIncoming = UserAdapter(requireContext(), users)
                        incomingRecyclerView.apply {
                            layoutManager = LinearLayoutManager(requireContext())
                            adapter = userAdapterIncoming
                        }
                    }
                }
            }
        }
    }
    private fun setListeners(event: Event) {

        attendSwitch.setOnCheckedChangeListener { _, isChecked ->
            CoroutineScope(Dispatchers.IO).launch {
                // Perform the database update
                Obj.updateUserAttendanceInEvent(event.eventCode, Obj.loggedUserID, isChecked)

                // Update the UI on the main thread
                withContext(Dispatchers.Main) {
                    val currentUser = users.find { it.name == Obj.user.displayName }
                    currentUser?.let {
                        currentUser.attending = isChecked
                        userAdapterIncoming.notifyItemChanged(users.indexOf(currentUser))
                    }
                }
            }
        }

        modifyTimeButton.setOnClickListener {
            val intent = Intent(requireContext(), ChooseTimeActivity::class.java)
            Obj.event = event
            startActivity(intent)
        }

        leaveButton.setOnClickListener {
            Toast.makeText(context, "Event Left", Toast.LENGTH_SHORT).show()
            Obj.user.events.remove(event.eventCode)
            Obj.removeUserFromEventAndParticipants(event, Obj.loggedUserID)
            fragmentManager?.popBackStack()
        }



        modifyEventButton.setOnClickListener {
            val intent = Intent(requireContext(), ChangeTimeActivity::class.java)
            startActivity(intent)
        }

        shareEventButton.setOnClickListener {
            Toast.makeText(context, "There is nothing to share >:(, yet :)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayEventDetails(event: Event?) {

        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

        event?.let { details ->
            eventDetailView.text = details.eventName
            var startStr = "null"
            var endStr = "null"
            if (details.startTime != null && details.startTime != "null") {
                startStr = LocalTime.parse(details.startTime, DateTimeFormatter.ISO_LOCAL_TIME)
                    .format(timeFormatter)
            }
            if (details.endTime != null && details.endTime != "null") {
                endStr = LocalTime.parse(details.endTime, DateTimeFormatter.ISO_LOCAL_TIME)
                    .format(timeFormatter)
            }

            if (details.endTime != null) {
                "${startStr} - ${endStr}".also {
                    timeDetailView.text = it
                }
            } else {
                "${startStr}".also {
                    timeDetailView.text = it
                }
            }


            var dateStr = "null"
            if (details.date != "" && details.date != null) {
                dateStr = LocalDate.parse(details.date, DateTimeFormatter.ISO_LOCAL_DATE)
                    .format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
            }

            dateDetailView.text = dateStr
            locationDetailView.text = details.locationName
            if (details.address == "") {
                addressDetailView.text = details.getCombinedAddress()
            } else {
                addressDetailView.text = details.address
            }
            "Feels like: ${details.feelLike.toString()}°F".also { feelLikeTextView.text = it }
            "Humidity: ${details.humidity.toString()}%".also { humidityTextView.text = it }
            "Wind: ${details.windSpeed.toString()} mph".also { windTextView.text = it }
            // Load weather icon using Glide
            val weatherIconResId = when (details.weatherCondition) {
                "Clear" -> R.drawable.sunny_icon
                "Clouds", "Mist", "Haze", "Fog" -> R.drawable.cloudy_icon
                "Rain", "Drizzle" -> R.drawable.rainy_icon
                "Thunderstorm" -> R.drawable.stormy_icon
                "Snow" -> R.drawable.snowy_icon
                else -> R.drawable.default_icon
            }
            Glide.with(this).load(weatherIconResId).into(weatherIconDetailView)
        }
    }
}