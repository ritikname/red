package com.lalkitab.deterministic.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RuleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRules(rules: List<LalKitabRule>)

    @Query("SELECT * FROM rules")
    suspend fun getAllRules(): List<LalKitabRule>

    @Query("SELECT COUNT(*) FROM rules")
    suspend fun getRuleCount(): Int
}
