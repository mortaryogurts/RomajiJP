package com.example.romajijp.db

import androidx.room.*

@Dao
interface SongDao {
    @Query("SELECT * FROM song_cache WHERE cacheId = :cacheId")
    suspend fun getSong(cacheId: String): SongCache?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: SongCache)

    @Query("SELECT * FROM song_cache WHERE isSaved = 1 ORDER BY timestamp DESC")
    suspend fun getAllSongs(): List<SongCache>

    @Update
    suspend fun updateSong(song: SongCache)

    @Delete
    suspend fun deleteSong(song: SongCache)
}