package com.lalkitab.deterministic.engine

import com.lalkitab.deterministic.data.LalKitabRule

data class KundaliInput(
    val planetsInHouses: Map<String, String>,
    val type: String,
    val age: Int? = null,
    val combinations: List<String> = emptyList()
)

data class EngineResult(
    val matchedRules: List<MatchedRule>,
    val rejectedRules: List<RejectedRule>,
    val finalPredictions: List<String>
)

data class MatchedRule(
    val rule: LalKitabRule,
    val evidenceLevel: String,
    val explanation: String
)

data class RejectedRule(
    val rule: LalKitabRule,
    val reason: String
)
