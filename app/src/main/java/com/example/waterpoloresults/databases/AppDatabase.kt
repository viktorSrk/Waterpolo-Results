package com.example.waterpoloresults.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.waterpoloresults.commons.League
import com.example.waterpoloresults.daos.LeagueDao

@Database(entities = [League::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun leagueDao(): LeagueDao
}