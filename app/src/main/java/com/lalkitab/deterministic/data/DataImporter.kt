package com.lalkitab.deterministic.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStreamReader
import java.io.FileInputStream

object DataImporter {
    suspend fun importDataIfEmpty(context: Context, database: AppDatabase) {
        withContext(Dispatchers.IO) {
            val ruleDao = database.ruleDao()
            if (ruleDao.getRuleCount() == 0) {
                val gson = Gson()
                val listType = object : TypeToken<List<LalKitabRule>>() {}.type

                // Read from /data/ at root of repo (packaged as assets during build, or accessed directly if on device)
                // For this offline build, we assume the JSON files are placed in app/src/main/assets/ by the build process.

                try {
                    val stream1952 = context.assets.open("lal_kitab_rules_1952_validated.json")
                    val reader1952 = InputStreamReader(stream1952)
                    val rules1952: List<LalKitabRule> = gson.fromJson(reader1952, listType)
                    reader1952.close()

                    val stream1942 = context.assets.open("lal_kitab_rules_1942_validated.json")
                    val reader1942 = InputStreamReader(stream1942)
                    val rules1942: List<LalKitabRule> = gson.fromJson(reader1942, listType)
                    reader1942.close()

                    val allRules = rules1952 + rules1942
                    ruleDao.insertRules(allRules)
                } catch (e: Exception) {
                    // Do not silently ignore. Re-throw to crash fast or let the UI handle it.
                    throw RuntimeException("CRITICAL: Source JSON data files (lal_kitab_rules_1952_validated.json and lal_kitab_rules_1942_validated.json) MUST be present in app/src/main/assets/. Please download from Drive and place them.", e)
                }
            }
        }
    }
}
