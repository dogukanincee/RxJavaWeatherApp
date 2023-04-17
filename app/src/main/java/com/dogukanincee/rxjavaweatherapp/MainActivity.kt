package com.dogukanincee.rxjavaweatherapp

import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

/**
 * Main activity for the weather app.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var locationEditText: EditText
    private lateinit var fetchButton: Button
    private lateinit var temperatureTextView: TextView
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        locationEditText = findViewById(R.id.locationEditText)
        fetchButton = findViewById(R.id.fetchButton)
        temperatureTextView = findViewById(R.id.temperatureTextView)

        // Set listener for the fetch button
        fetchButton.setOnClickListener { fetchWeather() }
    }

    /**
     * Returns a [Single] that emits the temperature at the given location.
     * Uses the Open Meteo API to get the temperature data.
     *
     * @param latitude The latitude of the location.
     * @param longitude The longitude of the location.
     * @return A [Single] that emits the temperature at the given location.
     */
    private fun getTemperature(latitude: Double, longitude: Double): Single<Double> {
        val service = retrofit.create(TemperatureService::class.java)

        return service.getTemperature(latitude, longitude, "temperature_2m")
            .map { response ->
                // Get the last temperature value from the response
                response.hourly.temperature_2m.lastOrNull()?.toDouble()
                // If there is no temperature data in the response, throw an exception
                    ?: throw Exception("No temperature data found for the given location.")
            }
    }

    /**
     * Returns a [Single] that emits the temperature at the given location
     * and executes the operation on the IO thread and observes on the main thread.
     *
     * @param latitude The latitude of the location.
     * @param longitude The longitude of the location.
     * @return A [Single] that emits the temperature at the given location.
     */
    private fun getTemperatureObservable(latitude: Double, longitude: Double): Single<Double> {
        return getTemperature(latitude, longitude)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * Fetches the temperature for the location entered in the EditText view.
     * If the location is blank, shows a Toast message and logs an error.
     * If the location is not found, shows a Toast message and logs an error.
     * If the location is found, fetches the temperature and updates the temperatureTextView with the result.
     */
    private fun fetchWeather() {
        // Get the location entered in the EditText view
        val location = locationEditText.text.toString()

        // Check if the location is blank
        if (location.isBlank()) {
            // Show a Toast message and log an error
            Toast.makeText(this, "Please enter a location", Toast.LENGTH_SHORT).show()
            Log.e("MainActivity", "Location is blank")
            return
        }

        // Use Geocoder to find the latitude and longitude of the location
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocationName(location, 1)

        // Check if any location was found
        if (addresses != null && addresses.isNotEmpty()) {
            // Get the first address in the list
            val address = addresses[0]
            val latitude = address.latitude
            val longitude = address.longitude

            // Log the location and coordinates being fetched
            Log.d(
                "MainActivity",
                "Fetching temperature for location=$location, latitude=$latitude, longitude=$longitude"
            )

            // Fetch the temperature from the API using the latitude and longitude
            val temperatureObservable = getTemperatureObservable(latitude, longitude)

            // Subscribe to the temperatureObservable and update the temperatureTextView with the result
            temperatureObservable.subscribeBy(
                onSuccess = { temperature ->
                    temperatureTextView.text = String.format("%.2f °C", temperature)
                },
                onError = { error ->
                    // Show a Toast message and log the error
                    Toast.makeText(
                        this,
                        "Error fetching temperature: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("MainActivity", "Error fetching temperature: ${error.message}")
                }
            )
        } else {
            // Show a Toast message and log an error
            Toast.makeText(this, "No location found for the given input", Toast.LENGTH_SHORT).show()
            Log.e("MainActivity", "No location found for the given input: $location")
        }
    }
}