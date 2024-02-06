package com.example.waterpoloresults.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.waterpoloresults.commons.League
import kotlinx.coroutines.flow.Flow

@Dao
interface LeagueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg leagues: League)

    @Delete
    suspend fun delete(league: League)

    @Query("""
        SELECT * FROM league
    """)
    fun getAll(): Flow<List<League>>

    @Query("""
        SELECT * FROM league
        WHERE leagueId = :leagueId
    """)
    fun getLeagueByLeagueId(leagueId: Int): Flow<League>
}