package com.lalkitab.deterministic.engine

import com.lalkitab.deterministic.data.LalKitabRule

class RuleEngine(private val allRules: List<LalKitabRule>) {

    fun evaluate(input: KundaliInput): EngineResult {
        val matched = mutableListOf<MatchedRule>()
        val rejected = mutableListOf<RejectedRule>()

        val contextRules = allRules.filter {
            it.context.equals(input.type, ignoreCase = true) || it.context == null
        }

        for (rule in contextRules) {
            if (matchesInput(rule, input)) {
                matched.add(MatchedRule(rule, "EXPLICIT", rule.source_text))
            } else {
                if (hasPartialMatch(rule, input)) {
                    rejected.add(RejectedRule(rule, "Condition not fully met by input."))
                }
            }
        }

        val rulesToRemove = mutableSetOf<String>()
        for (match in matched) {
            val exceptionTo = match.rule.exception_to
            if (!exceptionTo.isNullOrEmpty()) {
                val baseRuleIds = exceptionTo.split(",").map { it.trim() }
                rulesToRemove.addAll(baseRuleIds)
            }

            val contradiction = match.rule.contradiction
            if (!contradiction.isNullOrEmpty()) {
                val baseRuleIds = contradiction.split(",").map { it.trim() }
                rulesToRemove.addAll(baseRuleIds)
            }
        }

        val finalMatched = matched.filter { it.rule.rule_id !in rulesToRemove }
        val finalRejected = rejected + matched.filter { it.rule.rule_id in rulesToRemove }.map {
            RejectedRule(it.rule, "Overridden by Exception or Contradiction (e.g. 1952 takes priority).")
        }

        val predictions = finalMatched.map { it.explanation }

        return EngineResult(finalMatched, finalRejected, predictions)
    }

    private fun matchesInput(rule: LalKitabRule, input: KundaliInput): Boolean {
        val rulePlanets = rule.planets?.split(",")?.map { it.trim().lowercase() } ?: return false
        val ruleHouses = rule.houses?.split(",")?.map { it.trim() } ?: return false

        for (i in rulePlanets.indices) {
            val planet = rulePlanets[i]
            val house = if (ruleHouses.size > i) ruleHouses[i] else ruleHouses.last()

            val inputHouse = input.planetsInHouses.entries.find { it.key.lowercase() == planet }?.value
            if (inputHouse != house) {
                return false
            }
        }

        return true
    }

    private fun hasPartialMatch(rule: LalKitabRule, input: KundaliInput): Boolean {
        val rulePlanets = rule.planets?.split(",")?.map { it.trim().lowercase() } ?: return false
        return input.planetsInHouses.keys.any { it.lowercase() in rulePlanets }
    }
}
