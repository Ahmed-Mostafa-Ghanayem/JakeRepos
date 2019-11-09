package com.github.jake.repos.data.sources

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.jake.repos.entities.Repo

@Database(entities = [Repo::class], version = 1)
abstract class Database : RoomDatabase() {

    abstract fun repoDao(): ILocalDataSource
}