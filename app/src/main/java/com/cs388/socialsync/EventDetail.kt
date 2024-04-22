package com.cs388.socialsync

import android.app.AlarmManager
import android.app.PendingIntent
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
import androidx.appcompat.app.AppCompatActivity
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
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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
    lateinit var btnNotifyEvent: AppCompatButton

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
        val event = arguments?.getSerializable(EVENT_ITEM) as Event
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
        btnNotifyEvent = view.findViewById(R.id.btnNotifyEvent)

        btnNotifyEvent.setOnClickListener {
            Toast.makeText(activity, "Reminder Set", Toast.LENGTH_SHORT).show()

            val intent = Intent(activity, ReminderBroadcast::class.java)

            intent.putExtra("msg", eventDetailView.text.toString())
            intent.extras!!.putString("test", "hello")

            val pendingIntent = PendingIntent.getBroadcast(
                activity, (Math.random() * 1000).toInt(), intent,
                PendingIntent.FLAG_MUTABLE
            )


            val alarmManager =
                context?.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager

            val timeAtButtonClick = System.currentTimeMillis()
            val format = SimpleDateFormat("MM/dd/yyyy", Locale.US)
            val date = format.parse(dateDetailView.text.toString())
            val timeInMilliEventDate = date!!.time


            val time = timeDetailView.text.toString().split("-")[0]
            val timeInMilli = timeStringToMillis(time)
//            val timeInMilli = timeStringToMillis("04:00 PM")


            Log.e("CUSTOM=======>", timeInMilli.toString())
            Log.e("CUSTOM=======>", timeInMilliEventDate.toString())
            val aa = timeInMilliEventDate + timeInMilli
            Log.e("CUSTOM=======>", aa.toString())




            val deductMillies = 1000 * 1 * 60 * 30
            val bb = aa - deductMillies
            Log.e("TIME====>aa", convertMillisToDateTime(aa))
            Log.e("TIME====>bb", convertMillisToDateTime(bb))

            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                bb,
                pendingIntent
            )

        }
    }


    fun convertMillisToDateTime(millis: Long): String {
        val date = Date(millis)
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US)
        return format.format(date)
    }


    fun timeStringToMillis(timeString: String): Long {
        val format = SimpleDateFormat("hh:mm a", Locale.US)

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = 0
            time = format.parse(timeString) ?: return -1
        }

        Log.e("CUSTOM==", calendar.toString())
        Log.e("CUSTOM==", calendar.time.toString())
        Log.e("CUSTOM==", calendar.timeZone.toString())
        return (calendar.timeInMillis - (1000 * 1 * 60 * 60 * 5))
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
            Toast.makeText(context, "There is nothing to share yet", Toast.LENGTH_SHORT)
                .show()
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
                dateStr = try {
                    LocalDate.parse(details.date, DateTimeFormatter.ISO_LOCAL_DATE)
                        .format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
                } catch (e: DateTimeParseException) {
                    when (details.date) {
                        "MON" -> "Monday"
                        "TUE" -> "Tuesday"
                        "WED" -> "Wednesday"
                        "THU" -> "Thursday"
                        "FRI" -> "Friday"
                        "SAT" -> "Saturday"
                        "SUN" -> "Sunday"
                        else -> "Invalid day"
                    }
                }
            }

            dateDetailView.text = dateStr
            locationDetailView.text = details.locationName
            if (details.address == "") {
                addressDetailView.text = details.getCombinedAddress()
            } else {
                addressDetailView.text = details.address
            }
            if (!event.isInPerson && !event.isAPI) {
                "Feels like: N/A".also { feelLikeTextView.text = it }
                "Humidity: N/A".also { humidityTextView.text = it }
                "Wind: N/A".also { windTextView.text = it }
                Glide.with(this).load(R.drawable.virtual_icon).into(weatherIconDetailView)
            } else {
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
}