package com.example.lib

import java.util.UUID

data class Photo(
    val id: String = UUID.randomUUID().toString().replace("-", ""),
    val landmarkId: String,
    val filePath: String,
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double
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