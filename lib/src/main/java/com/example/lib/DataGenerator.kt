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

    fun generateRealMariborLandmarks(): List<Landmark> {
        return listOf(
            Landmark(
                name = "Red Bridge (Rdeči most)",
                description = "Historic red steel bridge spanning the Drava river, built in 1913. A symbol of Maribor connecting the old town with the northern districts.",
                latitude = 46.555891,
                longitude = 15.645931,
                category = LandmarkCategory.MONUMENT,
                pointValue = 150,
                city = "Maribor",
                address = "Koroška cesta"
            ),
            
            Landmark(
                name = "Plague Column",
                description = "Baroque monument from 1743 on Main Square, dedicated to the Holy Trinity. Built to commemorate the end of the plague epidemic.",
                latitude = 46.557630,
                longitude = 15.645454,
                category = LandmarkCategory.MONUMENT,
                pointValue = 120,
                city = "Maribor",
                address = "Glavni trg"
            ),
            
            Landmark(
                name = "Water Tower (Vodni stolp)",
                description = "Medieval defense tower from the 16th century, part of Maribor's former city walls. Now houses a wine shop.",
                latitude = 46.556396,
                longitude = 15.648369,
                category = LandmarkCategory.MONUMENT,
                pointValue = 130,
                city = "Maribor",
                address = "Usnjarska ulica 10"
            ),
            
            Landmark(
                name = "Judgement Tower (Sodni stolp)",
                description = "Renaissance tower from 1310, once served as a prison. Features distinctive clock and Venetian lion relief.",
                latitude = 46.556855,
                longitude = 15.641291,
                category = LandmarkCategory.MONUMENT,
                pointValue = 140,
                city = "Maribor",
                address = "Glavni trg 14"
            ),

            Landmark(
                name = "Maribor Castle",
                description = "15th-century castle housing the Maribor Regional Museum. Beautiful Renaissance architecture with exhibits on regional history.",
                latitude = 46.560771,
                longitude = 15.648520,
                category = LandmarkCategory.HISTORICAL,
                pointValue = 180,
                city = "Maribor",
                address = "Grajska ulica 2"
            ),
            
            Landmark(
                name = "Old Vine House (Hiša Stare trte)",
                description = "Home to the world's oldest vine, over 400 years old. Guinness World Record holder, still producing grapes.",
                latitude = 46.556717,
                longitude = 15.644228,
                category = LandmarkCategory.HISTORICAL,
                pointValue = 200,
                city = "Maribor",
                address = "Vojašniška ulica 8"
            ),
            
            Landmark(
                name = "Lent District",
                description = "Historic riverside district with medieval architecture. Site of the famous Lent Festival, Europe's largest outdoor festival.",
                latitude = 46.556769,
                longitude = 15.643788,
                category = LandmarkCategory.HISTORICAL,
                pointValue = 160,
                city = "Maribor",
                address = "Lent"
            ),

            Landmark(
                name = "Liberation Monument",
                description = "Monument commemorating liberation in WWII, located on the bank of Drava river. Important historical landmark.",
                latitude = 46.560472,
                longitude = 15.649126,
                category = LandmarkCategory.HISTORICAL,
                pointValue = 110,
                city = "Maribor",
                address = "Koroška cesta"
            ),

            Landmark(
                name = "City Park (Mestni park)",
                description = "Beautiful 19th-century English-style park with ponds, fountains, monuments, and diverse flora. Perfect for relaxation.",
                latitude = 46.566267,
                longitude = 15.647302,
                category = LandmarkCategory.PARK,
                pointValue = 100,
                city = "Maribor",
                address = "Mladinska ulica 1"
            ),
            
            Landmark(
                name = "Three Ponds (Trije ribniki)",
                description = "Scenic nature park with three interconnected ponds, walking trails, and recreational areas. Popular for picnics and walks.",
                latitude = 46.571448,
                longitude = 15.646591,
                category = LandmarkCategory.PARK,
                pointValue = 90,
                city = "Maribor",
                address = "Razvanjska ulica"
            ),
            
            Landmark(
                name = "Piramida Hill",
                description = "Pyramid-shaped hill offering panoramic views of Maribor and surrounding wine regions. Popular hiking destination.",
                latitude = 46.568122,
                longitude = 15.652345,
                category = LandmarkCategory.VIEWPOINT,
                pointValue = 170,
                city = "Maribor",
                address = "Piramida"
            ),
            
            Landmark(
                name = "Kalvarija Hill",
                description = "Hill with Stations of the Cross leading to a chapel. Offers excellent views of the city and Pohorje mountains.",
                latitude = 46.569162,
                longitude = 15.639800,
                category = LandmarkCategory.VIEWPOINT,
                pointValue = 150,
                city = "Maribor",
                address = "Ob Kalvariji"
            ),

            Landmark(
                name = "Maribor Synagogue",
                description = "Second oldest synagogue in Europe, built in 1429. Now serves as a cultural center hosting exhibitions and events.",
                latitude = 46.556747,
                longitude = 15.647675,
                category = LandmarkCategory.CULTURAL,
                pointValue = 190,
                city = "Maribor",
                address = "Židovska ulica 4"
            ),
            
            Landmark(
                name = "Main Square (Glavni trg)",
                description = "Central square of Maribor's old town, surrounded by beautiful baroque buildings, cafes, and the Plague Column.",
                latitude = 46.557608,
                longitude = 15.645293,
                category = LandmarkCategory.CULTURAL,
                pointValue = 140,
                city = "Maribor",
                address = "Glavni trg"
            ),
            
            Landmark(
                name = "Slomšek Square (Slomškov trg)",
                description = "Square in front of Maribor Cathedral, named after Bishop Anton Martin Slomšek. Important cultural and religious center.",
                latitude = 46.559203,
                longitude = 15.643869,
                category = LandmarkCategory.CULTURAL,
                pointValue = 120,
                city = "Maribor",
                address = "Slomškov trg"
            ),

            Landmark(
                name = "Maribor Cathedral",
                description = "Gothic cathedral dedicated to St. John the Baptist. Features beautiful frescoes and 57-meter bell tower.",
                latitude = 46.559087,
                longitude = 15.645022,
                category = LandmarkCategory.RELIGIOUS,
                pointValue = 160,
                city = "Maribor",
                address = "Slomškov trg 20"
            ),
            
            Landmark(
                name = "Franciscan Church",
                description = "Church and monastery with stunning baroque interior. Part of the Franciscan monastery complex dating to 1892.",
                latitude = 46.559958,
                longitude = 15.649984,
                category = LandmarkCategory.RELIGIOUS,
                pointValue = 130,
                city = "Maribor",
                address = "Franciscan Church, Maribor"
            ),

            Landmark(
                name = "Maribor City Hall",
                description = "Renaissance-style town hall with distinctive facade. Seat of city government and important administrative building.",
                latitude = 46.562505,
                longitude = 15.649767,
                category = LandmarkCategory.ARCHITECTURAL,
                pointValue = 140,
                city = "Maribor",
                address = "Glavni trg 14"
            ),
            
            Landmark(
                name = "University Library",
                description = "Modern architectural landmark designed by Slovenian architect Marko Mušič. Important academic and cultural institution.",
                latitude = 46.559198,
                longitude = 15.642745,
                category = LandmarkCategory.ARCHITECTURAL,
                pointValue = 110,
                city = "Maribor",
                address = "Gospejna ulica 10"
            ),

            Landmark(
                name = "Maribor Island (Mariborski otok)",
                description = "Artificial island on the Drava river with sports facilities, beaches, and recreational areas. Popular summer destination.",
                latitude = 46.567115,
                longitude = 15.611819,
                category = LandmarkCategory.OTHER,
                pointValue = 100,
                city = "Maribor",
                address = "Mladinska ulica"
            )
        )
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
