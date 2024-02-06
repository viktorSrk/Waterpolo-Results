package com.example.waterpoloresults.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.waterpoloresults.commons.Game
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg games: Game)

    @Delete
    suspend fun delete(game: Game)

    @Query(
        """
        SELECT * FROM game
        WHERE leagueId = :leagueId
            AND leagueGroup LIKE :leagueGroup
            AND leagueKind LIKE :leagueKind
            AND season = :season
    """
    )
    fun getAllGamesOfLeagueSeason(leagueId: Int, leagueGroup: String, leagueKind: String, season: Int): Flow<List<Game>>
}