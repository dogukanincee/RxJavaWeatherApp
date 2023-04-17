package com.dogukanincee.rxjavaweatherapp

/**
 * Data class representing the hourly temperature and time for a location.
 * @property temperature_2m List of hourly temperature values.
 * @property time List of hourly time values.
 */
data class Hourly(
    val temperature_2m: List<Float>,
    val time: List<String>
)
