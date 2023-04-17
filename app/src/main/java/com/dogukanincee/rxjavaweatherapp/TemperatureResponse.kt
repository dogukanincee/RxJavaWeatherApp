package com.dogukanincee.rxjavaweatherapp

data class TemperatureResponse(
    val latitude: Double,
    val longitude: Double,
    val hourly: Hourly
)
