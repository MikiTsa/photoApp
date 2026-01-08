package com.example.sliknisi.utils

import android.location.Location

object DistanceUtils {
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }

    fun formatDistance(distanceMeters: Float): String {
        return if (distanceMeters < 1000) {
            "${distanceMeters.toInt()} m"
        } else {
            "%.1f km".format(distanceMeters / 1000)
        }
    }

    fun formatDistanceWithSuffix(distanceMeters: Float): String {
        return if (distanceMeters < 1000) {
            "${distanceMeters.toInt()}m away"
        } else {
            "%.1f km away".format(distanceMeters / 1000)
        }
    }
}
