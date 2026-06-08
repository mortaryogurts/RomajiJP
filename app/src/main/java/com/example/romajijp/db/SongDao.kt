package com.example.romajijp.db

import androidx.room.*

@Dao
interface SongDao {
    @Query("SELECT * FROM song_cache WHERE cacheId = :cacheId")
    suspend fun getSong(cacheId: String): SongCache?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: SongCache)

    @Query("SELECT * FROM song_cache ORDER BY timestamp DESC")
    suspend fun getAllSongs(): List<SongCache>
}