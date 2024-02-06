package com.example.waterpoloresults.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.waterpoloresults.commons.*
import com.example.waterpoloresults.daos.*

@Database(entities = [League::class, Game::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun leagueDao(): LeagueDao
    abstract fun gameDao(): GameDao
}