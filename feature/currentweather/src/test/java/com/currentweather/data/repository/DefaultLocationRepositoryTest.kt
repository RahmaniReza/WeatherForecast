package com.currentweather.data.repository

import com.location.LocationProvider
import android.location.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultLocationRepositoryTest {

    private class FakeLocationProvider(
        var permissionsGranted: Boolean = true,
        var locationEnabled: Boolean = true,
        var lastLocation: Location? = null,
        var cityLocation: Location? = null
    ) : LocationProvider {
        override fun getLocationUpdates(): Flow<Location> = flowOf()
        override suspend fun getLastKnownLocation(): Location? = lastLocation
        override fun isLocationEnabled(): Flow<Boolean> = flowOf(locationEnabled)
        override fun hasLocationPermissions(): Boolean = permissionsGranted
        override suspend fun getCoordinatesForCity(cityName: String): Location? = cityLocation
    }

    @Test
    fun hasLocationPermissions_returnsExpectedValue() {
        val fakeProvider = FakeLocationProvider(permissionsGranted = true)
        val repository = DefaultLocationRepository(fakeProvider, Dispatchers.Unconfined)
        assertTrue(repository.hasLocationPermissions())

        fakeProvider.permissionsGranted = false
        assertFalse(repository.hasLocationPermissions())
    }

    @Test
    fun isLocationEnabled_emitsExpectedValue() = runBlocking {
        val fakeProvider = FakeLocationProvider(locationEnabled = true)
        val repository = DefaultLocationRepository(fakeProvider, Dispatchers.Unconfined)
        assertTrue(repository.isLocationEnabled().first())
    }

    @Test
    fun getLastKnownLocation_returnsNullWhenNoLocation() = runBlocking {
        val fakeProvider = FakeLocationProvider(lastLocation = null)
        val repository = DefaultLocationRepository(fakeProvider, Dispatchers.Unconfined)
        assertEquals(null, repository.getLastKnownLocation())
    }
}