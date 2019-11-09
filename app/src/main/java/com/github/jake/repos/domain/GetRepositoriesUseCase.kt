package com.github.jake.repos.domain

import com.github.jake.repos.data.repositories.IRepository
import com.github.jake.repos.entities.Repo
import io.reactivex.Single

class GetRepositoriesUseCase(private val repository: IRepository) {

    fun execute(perPage: Int = 15, page: Int): Single<List<Repo>> =
        repository.getRepositories(page, perPage)
}