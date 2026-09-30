package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.InspectionRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface InspectionDao {
    @Query("SELECT * FROM inspections ORDER BY timestamp DESC")
    fun getAllInspections(): Flow<List<InspectionRecord>>

    @Query("SELECT * FROM inspections WHERE id = :id LIMIT 1")
    suspend fun getInspectionById(id: Long): InspectionRecord?

    @Query("SELECT * FROM inspections WHERE reportId = :reportId LIMIT 1")
    suspend fun getInspectionByReportId(reportId: String): InspectionRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInspection(record: InspectionRecord): Long

    @Update
    suspend fun updateInspection(record: InspectionRecord)

    @Query("DELETE FROM inspections WHERE id = :id")
    suspend fun deleteInspection(id: Long)

    @Query("SELECT COUNT(*) FROM inspections")
    fun getInspectionCount(): Flow<Int>
}
