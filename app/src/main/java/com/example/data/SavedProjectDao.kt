package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedProjectDao {
    @Query("SELECT * FROM saved_projects ORDER BY timestamp DESC")
    fun getAllProjects(): Flow<List<SavedProject>>

    @Query("SELECT * FROM saved_projects WHERE id = :id LIMIT 1")
    suspend fun getProjectById(id: Long): SavedProject?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: SavedProject): Long

    @Update
    suspend fun updateProject(project: SavedProject)

    @Delete
    suspend fun deleteProject(project: SavedProject)
}
