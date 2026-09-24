package com.lalkitab.deterministic

import com.lalkitab.deterministic.data.LalKitabRule
import com.lalkitab.deterministic.engine.KundaliInput
import com.lalkitab.deterministic.engine.RuleEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EngineTests {

    private fun mockRule(
        id: String,
        edition: Int = 1952,
        planets: String? = null,
        houses: String? = null,
        context: String? = "Janam Kundali",
        text: String = "",
        exceptionTo: String? = null,
        contradiction: String? = null
    ): LalKitabRule {
        return LalKitabRule(
            rule_id = id,
            edition = edition,
            page = 10,
            section = "Test",
            rule_type = "Base",
            subjects = null,
            planets = planets,
            houses = houses,
            context = context,
            source_text = text,
            qualifies = null,
            exception_to = exceptionTo,
            modifies = null,
            combination = null,
            contradiction = contradiction,
            timing = null,
            remedy = null
        )
    }

    @Test
    fun testExactMatching() {
        val rules = listOf(
            mockRule("1", planets = "Mars", houses = "8", text = "Mars in 8 gives bad results.")
        )
        val engine = RuleEngine(rules)
        val result = engine.evaluate(KundaliInput(mapOf("Mars" to "8"), type = "Janam Kundali"))

        assertEquals(1, result.matchedRules.size)
        assertEquals("1", result.matchedRules[0].rule.rule_id)
        assertTrue(result.finalPredictions.contains("Mars in 8 gives bad results."))
    }

    @Test
    fun testExceptionOverridesBaseRule() {
        val rules = listOf(
            mockRule("1", planets = "Mars", houses = "8", text = "Mars in 8 gives bad results."),
            mockRule("2", planets = "Mars,Jupiter", houses = "8,5", exceptionTo = "1", text = "Exception: if Jupiter in 5, Mars in 8 gives good results.")
        )
        val engine = RuleEngine(rules)
        val result = engine.evaluate(KundaliInput(mapOf("Mars" to "8", "Jupiter" to "5"), type = "Janam Kundali"))

        assertEquals(1, result.matchedRules.size)
        assertEquals("2", result.matchedRules[0].rule.rule_id)
        assertTrue(result.finalPredictions.contains("Exception: if Jupiter in 5, Mars in 8 gives good results."))
    }

    @Test
    fun test1952PrioritizedOver1942() {
        val rules = listOf(
            mockRule("1_1942", edition = 1942, planets = "Moon", houses = "2", text = "Moon in 2 is decent (1942)."),
            mockRule("1_1952", edition = 1952, contradiction = "1_1942", planets = "Moon", houses = "2", text = "Moon in 2 is excellent (1952).")
        )
        val engine = RuleEngine(rules)
        val result = engine.evaluate(KundaliInput(mapOf("Moon" to "2"), type = "Janam Kundali"))

        val matchedIds = result.matchedRules.map { it.rule.rule_id }
        assertTrue(matchedIds.contains("1_1952"))
        assertTrue(!matchedIds.contains("1_1942"))
    }

    @Test
    fun testVarshfalSeparation() {
        val rules = listOf(
            mockRule("1", context = "Janam Kundali", planets = "Sun", houses = "1", text = "Sun in 1 Janam"),
            mockRule("2", context = "Varshfal", planets = "Sun", houses = "1", text = "Sun in 1 Varshfal")
        )
        val engine = RuleEngine(rules)

        val janamResult = engine.evaluate(KundaliInput(mapOf("Sun" to "1"), type = "Janam Kundali"))
        assertEquals(1, janamResult.matchedRules.size)
        assertEquals("1", janamResult.matchedRules[0].rule.rule_id)

        val varshfalResult = engine.evaluate(KundaliInput(mapOf("Sun" to "1"), type = "Varshfal"))
        assertEquals(1, varshfalResult.matchedRules.size)
        assertEquals("2", varshfalResult.matchedRules[0].rule.rule_id)
    }

    @Test
    fun testNoHallucination() {
        val rules = listOf(
            mockRule("1", planets = "Venus", houses = "7", text = "Venus in 7.")
        )
        val engine = RuleEngine(rules)
        val result = engine.evaluate(KundaliInput(mapOf("Venus" to "8"), type = "Janam Kundali"))

        assertEquals(0, result.matchedRules.size)
        assertEquals(0, result.finalPredictions.size)
    }
}
