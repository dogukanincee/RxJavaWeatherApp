package com.dogukanincee.rxjavaweatherapp

/**
 * Data class representing the response received from the Open Meteo API for getting the temperature.
 * @property latitude The latitude of the location.
 * @property longitude The longitude of the location.
 * @property hourly The [Hourly] object containing the temperature data.
 */
data class TemperatureResponse(
    val latitude: Double,
    val longitude: Double,
    val hourly: Hourly
)
