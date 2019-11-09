package com.github.jake.repos.data.sources

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.jake.repos.entities.Repo

@Dao
interface ILocalDataSource {

    @Query("SELECT * FROM repository LIMIT :perPage OFFSET :offset")
    fun getRepositories(offset: Int, perPage: Int): List<Repo>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun saveRepositories(repos: List<Repo>)
}