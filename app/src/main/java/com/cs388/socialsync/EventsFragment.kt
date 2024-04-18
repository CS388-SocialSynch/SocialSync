package com.cs388.socialsync

import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
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
import java.util.Locale

class EventsFragment : Fragment() {


    private lateinit var binding: FragmentEventsBinding
    private lateinit var publicEventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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

    fun showProgress() {
        binding.pbMain.visibility = View.VISIBLE
        binding.publicEventsRecyclerView.visibility = View.GONE
    }

    fun hideProgress() {
        binding.pbMain.visibility = View.GONE
        binding.publicEventsRecyclerView.visibility = View.VISIBLE
    }

    fun fetchEvents(eventsList: ArrayList<Event>) {

        showProgress()

        val client = okhttp3.OkHttpClient()
        val request = okhttp3.Request.Builder()
            .url("https://api.predicthq.com/v1/events?country=US&location_around.origin=40.7357,-74.1724&location_around.offset=10km&location_around.scale=10km&limit=10")
            .addHeader("Authorization", "Bearer 4nt22lSWoSHgp9vJz9TRU3zhWjTkdfofuR3Luwol")
            .addHeader("Accept", "application/json").build()

        val queue = Volley.newRequestQueue(context)
        val url =
            "https://api.predicthq.com/v1/events?country=US&location_around.origin=40.7357,-74.1724&location_around.offset=10km&location_around.scale=10km&limit=10"
        val getRequest: StringRequest =
            object : StringRequest(Request.Method.GET, url, Response.Listener<String> { response ->
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


                    val locationArr = eventJson.getJSONArray("location")
                    var latitude: String = "40.7357";
                    var longitude: String = "-74.1724";
                    if (locationArr.length() > 0) {
                        latitude = locationArr.get(1).toString();
                        longitude = locationArr.get(0).toString();
                    }

                    val local = Locale("en_us", "United States");
                    val geocoder = Geocoder(requireContext(), local)
                    val maxResult = 1
                    var zipCode = "07103";

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                        geocoder.getFromLocation(latitude.toDouble(),
                            longitude.toDouble(),
                            maxResult,
                            //                        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
                            object : Geocoder.GeocodeListener {
                                override fun onGeocode(addresses: MutableList<Address>) {

                                    Log.d("Address: ", addresses.toString())
                                    Log.d("ZIP CODE: ", addresses[0].postalCode)
                                    zipCode = addresses[0].postalCode
                                }

                                override fun onError(errorMessage: String?) {
                                    super.onError(errorMessage)

                                }
                            })

                    }


                    val aa = startDateTime.split("T")
                    val date = aa[0]
                    val time = aa[1].split("Z")[0]

                    val aaaa = LocalTime.parse(time)

                    val startLocalTime =
                        aaaa.format(DateTimeFormatter.ISO_LOCAL_TIME) as String

                    val localDate = LocalDate.parse(date)
                        .format(DateTimeFormatter.ISO_DATE) as String


                    var endLocalTime: String? = null
                    if (eventJson.optString("predicted_end", "") != "") {
                        val endTime =
                            eventJson.optString("predicted_end", "TZ").split("T")[1].split("Z")[0]
                        endLocalTime =
                            LocalTime.parse(endTime).format(DateTimeFormatter.ISO_TIME)
                    }

                    val event = Event()

                    event.eventName = eventJson.optString("title")
                    event.startTime = startLocalTime
                    event.endTime = endLocalTime
                    event.address = formattedAddress
                    event.temperature = 57
                    event.weatherCondition = "Sunny"
                    event.isPublic = true
                    event.isAPI = true
                    event.date = localDate
                    event.addressZipcode = zipCode;

                    eventsList.add(event)
                }
                publicEventAdapter.notifyDataSetChanged()
                hideProgress()


            }, Response.ErrorListener {
                Log.e("CUSTOM---->", it.message.toString())
            }) {
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
