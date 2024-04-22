package com.cs388.socialsync

import android.util.Log
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class WeatherFetcher {

    private val client = OkHttpClient()

    fun fetchWeather(
        zipCode: String,
        callback: (
            weatherCondition: String,
            temperature: Int,
            humidity: Int,
            windSpeed: Int,
            feelsLike: Int
        ) -> Unit
    ) {
        val apiKey="fa6d931f8c7a3724f3e12471580b4794"
        val url =
            "https://api.openweathermap.org/data/2.5/weather?zip=$zipCode,us&appid=$apiKey&units=imperial"

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val responseData = response.body?.string()
                    responseData?.let {
                        val jsonObject = JSONObject(it)

                        // Extract weather condition
                        val weatherArray = jsonObject.getJSONArray("weather")
                        val weatherObject = weatherArray.getJSONObject(0)
                        val weatherCondition = weatherObject.getString("main")

                        // Extract temperature
                        val mainObject = jsonObject.getJSONObject("main")
                        val temperature = mainObject.getInt("temp")

                        // Extract humidity
                        val humidity = mainObject.getInt("humidity")

                        // Extract wind speed
                        val windObject = jsonObject.getJSONObject("wind")
                        val windSpeed = windObject.getInt("speed")

                        // Extract feels like temperature
                        val feelsLike = mainObject.getInt("feels_like")

                        callback(
                            weatherCondition,
                            temperature,
                            humidity,
                            windSpeed,
                            feelsLike
                        )
                    }
                }
            }
        })
    }
}
