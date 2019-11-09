package com.github.jake.repos.data.sources

import com.github.jake.repos.entities.Repo
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface IRemoteDataSource {

    @GET("users/JakeWharton/repos")
    fun getRepositories(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("sort") sort:String = "created",
        @Query("direction") direction: String = "asc"
    ): Single<List<Repo>>
}