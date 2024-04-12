package com.cs388.socialsync

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.cs388.socialsync.databinding.FragmentEventsBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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

        val events: ArrayList<Event> = ArrayList()

        publicEventAdapter = EventAdapter(requireContext(), events)
        binding.publicEventsRecyclerView.adapter = publicEventAdapter
        fetchEvents(events)

        return view
    }

    fun fetchEvents(eventsList: ArrayList<Event>) {

        val client = okhttp3.OkHttpClient()
        val request = okhttp3.Request.Builder()
            .url("https://api.predicthq.com/v1/events?country=US&location_around.origin=40.7357,-74.1724&location_around.offset=10km&location_around.scale=10km&limit=10")
            .addHeader("Authorization", "Bearer 4nt22lSWoSHgp9vJz9TRU3zhWjTkdfofuR3Luwol")
            .addHeader("Accept", "application/json")
            .build()

        val queue = Volley.newRequestQueue(context)
        val url =
            "https://api.predicthq.com/v1/events?country=US&location_around.origin=40.7357,-74.1724&location_around.offset=10km&location_around.scale=10km&limit=10"
        val getRequest: StringRequest = object : StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                Log.e("CUSTOM---->", response)
                val json = JSONObject(response)
                val resultsArray = json.getJSONArray("results")
                Log.e("CUSTOM---->", resultsArray.getJSONObject(0).getString("title"))


                for (i in 0 until resultsArray.length()) {
                    val eventJson = resultsArray.getJSONObject(i)
                    val title = eventJson.getString("title")
                    val words = title.split(" ")
                    val displayTitle = if (words.size >= 4) {
                        words.take(4).joinToString(" ") + "..."
                    } else {
                        title
                    }
                    var formattedAddress = "Newark, 07102";
                    var locationName = "Campus Center"
                    val entitiesArray = eventJson.getJSONArray("entities")
                    if (entitiesArray.length() > 0) {
                        val entity = entitiesArray.getJSONObject(0)
                        formattedAddress = entity.getString("formatted_address")
                        locationName = entity.getString("name")
                        Log.e("Formatted Address", formattedAddress)
                    }
                    val startDateTime = eventJson.getString("start")
                    //val endDateTime = LocalTime.parse(eventJson.getString("end"), DateTimeFormatter.ISO_DATE_TIME)
                    //val formattedAddress = "unknown"
                    //val eventDate = LocalDate.parse(eventJson.getString("start"), DateTimeFormatter.ISO_DATE)


                    val event = Event(
                        eventName = displayTitle,
                        startTime = LocalTime.now(),
                        endTime = LocalTime.now(),
                        date = LocalDate.now(),
                        temperature = 57,
                        weatherCondition = "sunny",
                        locationName = locationName,
                        address = formattedAddress,
                        isHost = false,
                        isPublic = true,
                        showParticipants = false,
                        isInPerson = false,
                        hostUID = "",
                        optionStartTime = null,
                        optionEndTime = null,
                        specificDate = false
                    )
                    eventsList.add(event)
                }
                publicEventAdapter.notifyDataSetChanged()






            },
            Response.ErrorListener {
                Log.e("CUSTOM---->", it.message.toString())
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = "Bearer 4nt22lSWoSHgp9vJz9TRU3zhWjTkdfofuR3Luwol"
                params["Accept"] = "application/json"
                return params
            }
        }
        queue.add(getRequest)


    }







}
