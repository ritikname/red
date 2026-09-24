package com.lalkitab.deterministic.engine

object QueryParser {
    private val PLANETS = listOf("Sun", "Moon", "Mars", "Mercury", "Jupiter", "Venus", "Saturn", "Rahu", "Ketu")
    private val HOUSES = (1..12).map { it.toString() } + listOf("first", "second", "third", "fourth", "fifth", "sixth", "seventh", "eighth", "ninth", "tenth", "eleventh", "twelfth")

    fun parse(query: String): KundaliInput {
        val lowerQuery = query.lowercase()
        val planetsInHouses = mutableMapOf<String, String>()

        for (planet in PLANETS) {
            if (lowerQuery.contains(planet.lowercase())) {
                val words = lowerQuery.split(Regex("\\W+"))
                val planetIdx = words.indexOf(planet.lowercase())
                if (planetIdx != -1) {
                    for (i in planetIdx + 1 until minOf(planetIdx + 5, words.size)) {
                        if (words[i] in HOUSES) {
                            planetsInHouses[planet] = parseHouse(words[i])
                            break
                        }
                    }
                }
            }
        }

        val type = if (lowerQuery.contains("varshfal")) "Varshfal" else "Janam Kundali"

        return KundaliInput(
            planetsInHouses = planetsInHouses,
            type = type,
            age = null,
            combinations = emptyList()
        )
    }

    private fun parseHouse(word: String): String {
        return when (word) {
            "first" -> "1"
            "second" -> "2"
            "third" -> "3"
            "fourth" -> "4"
            "fifth" -> "5"
            "sixth" -> "6"
            "seventh" -> "7"
            "eighth" -> "8"
            "ninth" -> "9"
            "tenth" -> "10"
            "eleventh" -> "11"
            "twelfth" -> "12"
            else -> word
        }
    }
}
