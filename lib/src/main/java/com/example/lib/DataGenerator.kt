package com.example.lib

import io.github.serpro69.kfaker.Faker

object DataGenerator {

    private val faker = Faker()

    fun generateLandmarks(count: Int, city: String = "Maribor"): List<Landmark> {
        val landmarks = mutableListOf<Landmark>()

        val landmarkNames = listOf(
            "Maribor Castle", "Water Tower", "Old Vine House", "Cathedral of St. John",
            "Plague Monument", "Main Square", "Judgement Tower", "City Park",
            "Maribor Synagogue", "Liberation Monument", "Lent District", "Franciscan Church",
            "Piramida Hill", "Three Ponds", "University Library", "Poštna Street",
            "Slomšek Square", "Maribor Island", "Red Bridge", "City Hall"
        )

        // Base coordinates for Maribor
        val baseLatitude = 46.5547
        val baseLongitude = 15.6467

        for (i in 0 until count) {
            val name = if (i < landmarkNames.size) {
                landmarkNames[i]
            } else {
                "${landmarkNames.random()} ${i + 1}"
            }

            landmarks.add(
                Landmark(
                    name = name,
                    description = generateDescription(),
                    latitude = baseLatitude + kotlin.random.Random.nextDouble(-0.02, 0.02),
                    longitude = baseLongitude + kotlin.random.Random.nextDouble(-0.02, 0.02),
                    category = LandmarkCategory.values().random(),
                    pointValue = (50..200).random(),
                    city = city,
                    address = generateAddress()
                )
            )
        }

        return landmarks
    }

    fun generateAchievements(): List<Achievement> {
        return listOf(
            Achievement(
                id = "ach_001",
                name = "First Steps",
                description = "Visit your first landmark",
                requiredPoints = 0,
                requiredLandmarks = 1
            ),
            Achievement(
                id = "ach_002",
                name = "Explorer",
                description = "Visit 5 landmarks",
                requiredPoints = 250,
                requiredLandmarks = 5
            ),
            Achievement(
                id = "ach_003",
                name = "City Guide",
                description = "Visit 10 landmarks",
                requiredPoints = 1000,
                requiredLandmarks = 10
            ),
            Achievement(
                id = "ach_004",
                name = "Point Collector",
                description = "Earn 500 points",
                requiredPoints = 500,
                requiredLandmarks = 0
            ),
            Achievement(
                id = "ach_005",
                name = "Master Explorer",
                description = "Visit 20 landmarks",
                requiredPoints = 2000,
                requiredLandmarks = 20
            ),
            Achievement(
                id = "ach_006",
                name = "Level 5",
                description = "Reach level 5",
                requiredPoints = 500,
                requiredLandmarks = 0
            ),
            Achievement(
                id = "ach_007",
                name = "Park Lover",
                description = "Visit all parks",
                requiredPoints = 300,
                requiredLandmarks = 3
            ),
            Achievement(
                id = "ach_008",
                name = "History Buff",
                description = "Visit all historical landmarks",
                requiredPoints = 500,
                requiredLandmarks = 5
            )
        )
    }

    private fun generateDescription(): String {
        val templates = listOf(
            "A historic landmark dating back to ${(1200..1900).random()}.",
            "One of the most iconic spots in the city, known for its ${faker.commerce.productName()}.",
            "A beautiful ${faker.color.name()} structure showcasing ${faker.ancient.god()} architecture.",
            "Popular destination for tourists and locals alike.",
            "A hidden gem offering stunning views of the city.",
            "Rich in cultural and historical significance.",
            "A must-visit location capturing the essence of the city."
        )
        return templates.random()
    }

    private fun generateAddress(): String {
        val streets = listOf(
            "Glavni trg", "Gosposka ulica", "Slovenska ulica", "Koroška cesta",
            "Titova cesta", "Partizanska cesta", "Poštna ulica", "Vetrinjska ulica"
        )
        return "${streets.random()} ${(1..50).random()}"
    }
}