package com.github.jake.repos.presentation

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.github.jake.repos.domain.GetRepositoriesUseCase
import com.github.jake.repos.entities.Repo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

class RepositoriesViewModel(
    private val getRepositoriesUseCase: GetRepositoriesUseCase
) : ViewModel() {

    private var disposable: Disposable? = null
    private val liveData = MutableLiveData<UIState>()
    private val repos = ArrayList<Repo>()
    private var page = 1
    private val perPage = 15
    private var canLoadMore = true

    init {
        handleUIActions(UIAction.InitialAction)
    }

    @Suppress("REDUNDANT_ELSE_IN_WHEN")
    fun handleUIActions(action: UIAction) {
        when (action) {
            is UIAction.InitialAction,
            is UIAction.RefreshAction,
            is UIAction.RetryAction -> {
                page = 1
                canLoadMore = true
                if (action is UIAction.RetryAction) {
                    liveData.value = UIState.HideError
                }

                if (action is UIAction.InitialAction || action is UIAction.RetryAction) {
                    liveData.value = UIState.ShowLoading
                }
                loadRepositories(perPage, page, Consumer {
                    if (action is UIAction.RefreshAction) {
                        liveData.value = UIState.HideRefresh
                    } else {
                        liveData.value = UIState.HideLoading
                    }
                    repos.clear()
                    repos.addAll(it)
                    liveData.value = UIState.ShowData(repos)
                }, Consumer {
                    liveData.value = UIState.ShowError(it.message!!)
                })
            }

            is UIAction.LoadMoreAction,
            is UIAction.RetryLoadMoreAction -> {
                if (canLoadMore) {
                    page++
                    liveData.value = UIState.ShowLoadingMore
                    liveData.value = UIState.HideLoadMoreError
                    loadRepositories(perPage, page, Consumer {
                        liveData.value = UIState.HideLoadingMore
                        if (it.isEmpty()) {
                            canLoadMore = false
                        } else {
                            if (it.size < perPage) {
                                canLoadMore = false
                            }
                            repos.addAll(it)
                            liveData.value = UIState.NewData(
                                repos,
                                (page - 1) * perPage,
                                (page * perPage) - 1
                            )
                        }
                    }, Consumer {
                        liveData.value = UIState.HideLoadingMore
                        liveData.value = UIState.ShowLoadMoreError
                        page--
                    })
                }
            }

            else -> throw UnsupportedOperationException("This operation $action is not supported or not implemented yet!")
        }
    }

    @Suppress("SameParameterValue")
    private fun loadRepositories(
        perPage: Int,
        page: Int,
        onSuccess: Consumer<List<Repo>>,
        onError: Consumer<Throwable>
    ) {
        disposable = getRepositoriesUseCase.execute(perPage, page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onSuccess, onError)
    }

    fun observeOnUIState(owner: LifecycleOwner, observer: Observer<UIState>) {
        liveData.observe(owner, observer)
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.apply {
            if (isDisposed.not()) {
                dispose()
            }
        }
    }
}