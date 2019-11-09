package com.github.jake.repos.presentation

sealed class UIAction {

    object InitialAction : UIAction()
    object LoadMoreAction : UIAction()
    object RefreshAction : UIAction()
    object RetryAction : UIAction()
    object RetryLoadMoreAction : UIAction()
}