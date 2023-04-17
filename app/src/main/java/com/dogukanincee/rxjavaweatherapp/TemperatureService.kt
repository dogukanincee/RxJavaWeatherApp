package com.dogukanincee.rxjavaweatherapp

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * A Retrofit service interface for Open Meteo API to get temperature data.
 */
interface TemperatureService {
    /**
     * Returns a [Single] that emits the temperature response at the given location.
     * The API endpoint used is /forecast.
     * The query parameters include the latitude, longitude, and hourly data type, which defaults to "temperature_2m".
     * @param latitude The latitude of the location.
     * @param longitude The longitude of the location.
     * @param hourly The type of hourly data to fetch, defaults to "temperature_2m".
     * @return A [Single] that emits the temperature response at the given location.
     */
    @GET("forecast")
    fun getTemperature(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("hourly") hourly: String = "temperature_2m"
    ): Single<TemperatureResponse>
}

