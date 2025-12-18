package com.example.lib

data class Achievement(
    val id: String,
    val name: String,
    val description: String,
    val requiredPoints: Int,
    val requiredLandmarks: Int = 0,
    var isUnlocked: Boolean = false
) : Comparable<Achievement> {

    override fun compareTo(other: Achievement): Int {
        return requiredPoints.compareTo(other.requiredPoints)
    }

    override fun toString(): String {
        return """
            Achievement: $name
              Description: $description
              Required Points: $requiredPoints
              Required Landmarks: $requiredLandmarks
              Status: ${if (isUnlocked) "Unlocked" else "Locked"}
        """.trimIndent()
    }
}