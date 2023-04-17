package com.dogukanincee.rxjavaweatherapp

import android.location.Address
import android.location.Geocoder
import android.os.Build
import io.reactivex.rxjava3.core.Single
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class MainActivityTest {

    private lateinit var mainActivity: MainActivity

    @Mock
    private lateinit var mockGeocoder: Geocoder

    @Mock
    private lateinit var mockService: TemperatureService

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        mainActivity = Robolectric.setupActivity(MainActivity::class.java)
    }

    @Test
    fun fetchWeather_locationBlank_toastShownAndLogError() {
        // Given
        mainActivity.locationEditText.setText("")

        // When
        mainActivity.fetchWeather()

        // Then
        assertTrue(mainActivity.temperatureTextView.text.isBlank())
    }

    @Test
    fun fetchWeather_locationNotFound_toastShownAndLogError() {
        // Given
        val location = "Unknown Location"
        `when`(mockGeocoder.getFromLocationName(location, 1)).thenReturn(emptyList())

        mainActivity.locationEditText.setText(location)

        // When
        mainActivity.fetchWeather()

        // Then
        assertTrue(mainActivity.temperatureTextView.text.isBlank())
    }

    @Test
    fun fetchWeather_locationFound_fetchTemperatureAndUpdateTextView() {
        // Given
        val location = "Tokyo, Japan"
        val addresses = listOf(Address(null))
        addresses[0].latitude = 35.6895
        addresses[0].longitude = 139.6917

        `when`(mockGeocoder.getFromLocationName(location, 1)).thenReturn(addresses)

        mainActivity.locationEditText.setText(location)

        // When
        mainActivity.fetchWeather()

        // Then
        assertNotNull(mainActivity.temperatureTextView.text)
    }

    @Test
    fun fetchWeather_networkError_toastShownAndLogError() {
        // Given
        val location = "Tokyo, Japan"
        val addresses = listOf(Address(null))
        addresses[0].latitude = 35.6895
        addresses[0].longitude = 139.6917

        `when`(mockGeocoder.getFromLocationName(location, 1)).thenReturn(addresses)
        `when`(mockService.getTemperature(35.6895, 139.6917, "temperature_2m"))
            .thenReturn(Single.error(IOException("Network error")))

        mainActivity.locationEditText.setText(location)

        // When
        mainActivity.fetchWeather()

        // Then
        assertTrue(mainActivity.temperatureTextView.text.isBlank())
    }
}