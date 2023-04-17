package com.dogukanincee.rxjavaweatherapp

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface TemperatureService {
    @GET("forecast")
    fun getTemperature(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("hourly") hourly: String = "temperature_2m"
    ): Single<TemperatureResponse>
}

