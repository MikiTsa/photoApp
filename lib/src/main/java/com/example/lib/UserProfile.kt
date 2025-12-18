package com.example.lib

import java.util.UUID

data class UserProfile(
    val userId: String = UUID.randomUUID().toString().replace("-", ""),
    var username: String,
    var level: Int = 1,
    var totalPoints: Int = 0,
    var visitedLandmarkIds: MutableList<String> = mutableListOf(),
    var capturedPhotoIds: MutableList<String> = mutableListOf(),
    var unlockedAchievementIds: MutableList<String> = mutableListOf()
) {

    fun addPoints(points: Int) {
        totalPoints += points
        updateLevel()
    }

    private fun updateLevel() {
        // Every 100 points = 1 level
        level = (totalPoints / 100) + 1
    }

    fun visitLandmark(landmarkId: String, points: Int) {
        if (!visitedLandmarkIds.contains(landmarkId)) {
            visitedLandmarkIds.add(landmarkId)
            addPoints(points)
        }
    }

    override fun toString(): String {
        return """
            User Profile: $username
              User ID: $userId
              Level: $level
              Total Points: $totalPoints
              Landmarks Visited: ${visitedLandmarkIds.size}
              Photos Captured: ${capturedPhotoIds.size}
              Achievements Unlocked: ${unlockedAchievementIds.size}
        """.trimIndent()
    }
}