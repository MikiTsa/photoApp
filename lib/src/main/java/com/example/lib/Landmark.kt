package com.example.lib

import java.util.UUID

data class Landmark(
    val id: String = UUID.randomUUID().toString().replace("-", ""),
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val category: LandmarkCategory,
    val pointValue: Int,
    var isVisited: Boolean = false,
    var visitedDate: Long? = null,
    val city: String,
    val address: String
) : Comparable<Landmark> {

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