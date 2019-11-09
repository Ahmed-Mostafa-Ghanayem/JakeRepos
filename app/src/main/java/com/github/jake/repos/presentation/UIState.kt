package com.github.jake.repos.presentation

import com.github.jake.repos.entities.Repo

sealed class UIState {

    object ShowLoading : UIState()
    object HideLoading : UIState()
    object HideRefresh : UIState()
    object ShowLoadingMore : UIState()
    object HideLoadingMore : UIState()
    data class ShowError(val message: String) : UIState()
    object HideError : UIState()
    object ShowLoadMoreError : UIState()
    object HideLoadMoreError : UIState()
    data class ShowData(val repos: List<Repo>) : UIState()
    data class NewData(
        val repos: List<Repo>,
        val from: Int,
        val to: Int
    ) : UIState()
}