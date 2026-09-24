package com.lalkitab.deterministic.data

import android.content.Context
import android.util.Log
import org.json.JSONObject
import org.json.JSONArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

object DataImporter {
    suspend fun importDataIfEmpty(context: Context, database: AppDatabase) {
        withContext(Dispatchers.IO) {
            val ruleDao = database.ruleDao()
            if (ruleDao.getRuleCount() == 0) {

                try {
                    val stream1952 = context.assets.open("lal_kitab_rules_1952_validated.json")
                    val json1952 = stream1952.bufferedReader().use { it.readText() }
                    val rules1952 = parseJsonToRules(json1952)
                    stream1952.close()

                    val stream1942 = context.assets.open("lal_kitab_rules_1942_validated.json")
                    val json1942 = stream1942.bufferedReader().use { it.readText() }
                    val rules1942 = parseJsonToRules(json1942)
                    stream1942.close()

                    val allRules = rules1952 + rules1942
                    ruleDao.insertRules(allRules)
                    Log.d("DataImporter", "Inserted ${allRules.size} rules into the database.")
                } catch (e: Exception) {
                    throw RuntimeException("CRITICAL: Source JSON data files (lal_kitab_rules_1952_validated.json and lal_kitab_rules_1942_validated.json) MUST be present in app/src/main/assets/. Please download from Drive and place them.", e)
                }
            }
        }
    }

    private fun parseJsonToRules(jsonString: String): List<LalKitabRule> {
        val rulesList = mutableListOf<LalKitabRule>()
        val rootObj = JSONObject(jsonString)
        val rulesArray = rootObj.getJSONArray("rules")

        for (i in 0 until rulesArray.length()) {
            val obj = rulesArray.getJSONObject(i)

            val ruleId = obj.optString("RULE_ID", "")
            if (ruleId.isEmpty()) continue

            val editionStr = obj.optString("EDITION", "0")
            val edition = editionStr.toIntOrNull() ?: 0

            val pageStr = obj.optString("PAGE", "0")
            val page = pageStr.toIntOrNull() ?: 0

            val section = obj.optString("SECTION", null)?.takeIf { it != "null" }
            val ruleType = obj.optString("RULE_TYPE", null)?.takeIf { it != "null" }

            val subjectsArr = obj.optJSONArray("SUBJECTS")
            val subjects = if (subjectsArr != null && subjectsArr.length() > 0) {
                (0 until subjectsArr.length()).map { subjectsArr.getString(it) }.joinToString(",")
            } else null

            val planetsArr = obj.optJSONArray("PLANETS")
            val planets = if (planetsArr != null && planetsArr.length() > 0) {
                (0 until planetsArr.length()).map { planetsArr.getString(it) }.joinToString(",")
            } else null

            val housesArr = obj.optJSONArray("HOUSES")
            val houses = if (housesArr != null && housesArr.length() > 0) {
                (0 until housesArr.length()).map { housesArr.getString(it) }.joinToString(",")
            } else null

            val context = obj.optString("CONTEXT", null)?.takeIf { it != "null" }
            val sourceText = obj.optString("SOURCE_TEXT", "")

            rulesList.add(LalKitabRule(
                rule_id = ruleId,
                edition = edition,
                page = page,
                section = section,
                rule_type = ruleType,
                subjects = subjects,
                planets = planets,
                houses = houses,
                context = context,
                source_text = sourceText,
                qualifies = obj.optString("QUALIFIES", null)?.takeIf { it != "null" },
                exception_to = obj.optString("EXCEPTION_TO", null)?.takeIf { it != "null" },
                modifies = obj.optString("MODIFIES", null)?.takeIf { it != "null" },
                combination = obj.optString("COMBINATION", null)?.takeIf { it != "null" },
                contradiction = obj.optString("CONTRADICTION", null)?.takeIf { it != "null" },
                timing = obj.optString("TIMING", null)?.takeIf { it != "null" },
                remedy = obj.optString("REMEDY", null)?.takeIf { it != "null" }
            ))
        }
        return rulesList
    }
}
