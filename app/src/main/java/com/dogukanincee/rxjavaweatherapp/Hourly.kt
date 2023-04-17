package com.dogukanincee.rxjavaweatherapp

data class Hourly(
    val temperature_2m: List<Float>,
    val time: List<String>
)
