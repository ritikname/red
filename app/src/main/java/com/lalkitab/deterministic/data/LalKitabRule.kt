package com.lalkitab.deterministic.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rules")
data class LalKitabRule(
    @PrimaryKey
    val rule_id: String,
    val edition: Int,
    val page: Int,
    val section: String?,
    val rule_type: String?,
    val subjects: String?,
    val planets: String?,
    val houses: String?,
    val context: String?,
    val source_text: String,
    val qualifies: String?,
    val exception_to: String?,
    val modifies: String?,
    val combination: String?,
    val contradiction: String?,
    val timing: String?,
    val remedy: String?
)
