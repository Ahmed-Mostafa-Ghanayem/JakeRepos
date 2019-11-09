package com.github.jake.repos.data.repositories

import com.github.jake.repos.data.sources.ILocalDataSource
import com.github.jake.repos.data.sources.IRemoteDataSource
import com.github.jake.repos.entities.Repo
import io.reactivex.Single

class RepositoryImpl(
    private val remote: IRemoteDataSource,
    private val local: ILocalDataSource
) : IRepository {
    override fun getRepositories(page: Int, perPage: Int): Single<List<Repo>> {
        return if (page < 1) {
            Single.error(IllegalArgumentException("Invalid page value = $page, page value must start from 1"))
        } else {
            remote.getRepositories(page, perPage)
                .flatMap {
                    local.saveRepositories(it)
                    Single.just(it)
                }
                .onErrorResumeNext {
                    val repos = local.getRepositories((page - 1 * perPage) + 1, perPage)
                    if (repos.isNullOrEmpty()) {
                        Single.error(it)
                    } else {
                        Single.just(repos)
                    }
                }
        }
    }
}