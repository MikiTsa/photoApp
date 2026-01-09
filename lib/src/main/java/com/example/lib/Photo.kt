package com.example.lib

import java.util.UUID

data class Photo(
    val id: String = UUID.randomUUID().toString().replace("-", ""),
    val landmarkId: String = "",
    val filePath: String = "",
    val timestamp: Long = 0L,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
) {
    override fun toString(): String {
        return """
            Photo: $id
              Landmark ID: $landmarkId
              Path: $filePath
              Timestamp: $timestamp
              Location: ($latitude, $longitude)
        """.trimIndent()
    }
}