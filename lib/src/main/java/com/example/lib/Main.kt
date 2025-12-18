package com.example.lib

fun main() {
    println("=".repeat(60))
    println("SLIKNISI - TEST DATA GENERATION")
    println("=".repeat(60))

    val landmarks = DataGenerator.generateLandmarks(20, "Maribor")
    println("\n${landmarks.size} LANDMARKS GENERATED:\n")

    // display first 5 landmarks
    landmarks.take(5).forEach { landmark ->
        println(landmark)
        println("-".repeat(60))
    }

    val sortedLandmarks = landmarks.sorted()  // sort landmarks by points
    println("\nTOP 5 LANDMARKS BY POINTS:\n")
    sortedLandmarks.takeLast(5).forEach { landmark ->
        println("${landmark.name}: ${landmark.pointValue} points")
    }

    println("\n" + "=".repeat(60))
    val achievements = DataGenerator.generateAchievements()
    println("\n${achievements.size} ACHIEVEMENTS GENERATED:\n")
    achievements.forEach { achievement ->
        println(achievement)
        println("-".repeat(60))
    }

    println("\n" + "=".repeat(60))
    println("USER PROFILE TEST:\n")
    val user = UserProfile(username = "Mihail")
    println(user)

    println("\n--- Visiting landmarks ---")
    landmarks.take(3).forEach { landmark ->
        user.visitLandmark(landmark.id, landmark.pointValue)
        println("Visited: ${landmark.name} (+${landmark.pointValue} points)")
    }

    println("\n--- Updated Profile ---")
    println(user)

    println("\n" + "=".repeat(60))
}