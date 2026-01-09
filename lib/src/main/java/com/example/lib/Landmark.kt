package com.example.lib

import java.util.UUID

data class Landmark(
    val id: String = UUID.randomUUID().toString().replace("-", ""),
    var name: String = "",
    var description: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var category: LandmarkCategory = LandmarkCategory.OTHER,
    var pointValue: Int = 0,
    var city: String = "",
    var address: String = "",
    var isVisited: Boolean = false
): Comparable<Landmark> {

    override fun compareTo(other: Landmark): Int {
        return pointValue.compareTo(other.pointValue)
    }

    override fun toString(): String {
        return """
            Landmark: $name
              ID: $id
              Category: $category
              Location: ($latitude, $longitude)
              Address: $address, $city
              Points: $pointValue
              Visited: ${if (isVisited) "Yes" else "No"}
              Description: $description
        """.trimIndent()
    }
}