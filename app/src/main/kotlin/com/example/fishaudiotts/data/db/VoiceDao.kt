package com.example.fishaudiotts.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for voice operations
 */
@Dao
interface VoiceDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(voice: VoiceEntity)
    
    @Update
    suspend fun update(voice: VoiceEntity)
    
    @Delete
    suspend fun delete(voice: VoiceEntity)
    
    @Query("SELECT * FROM favorite_voices WHERE id = :id")
    suspend fun getVoiceById(id: String): VoiceEntity?
    
    @Query("SELECT * FROM favorite_voices ORDER BY createdAt DESC")
    fun getAllVoices(): Flow<List<VoiceEntity>>
    
    @Query("SELECT * FROM favorite_voices WHERE isDefault = 1 LIMIT 1")
    fun getDefaultVoice(): Flow<VoiceEntity?>
    
    @Query("SELECT COUNT(*) FROM favorite_voices")
    fun getVoiceCount(): Flow<Int>
    
    @Query("UPDATE favorite_voices SET isDefault = 0 WHERE isDefault = 1")
    suspend fun clearDefaultVoices()
    
    @Query("UPDATE favorite_voices SET isDefault = 1 WHERE id = :id")
    suspend fun setDefaultVoice(id: String)
    
    @Query("DELETE FROM favorite_voices WHERE id = :id")
    suspend fun deleteVoiceById(id: String)
}
