package com.github.jake.repos.data.repositories

import com.github.jake.repos.entities.Repo
import io.reactivex.Single

interface IRepository {

    fun getRepositories(page: Int, perPage: Int): Single<List<Repo>>
}