package com.github.jake.repos.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.jake.repos.R
import com.github.jake.repos.entities.Repo
import com.github.jake.repos.entities.getSizeAndType
import kotlinx.android.synthetic.main.fragment_repositories.*
import kotlinx.android.synthetic.main.item_repository.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class RepositoriesFragment : Fragment() {

    private val repositoriesViewModel by viewModel<RepositoriesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_repositories, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvLoadMoreError.setOnClickListener {
            repositoriesViewModel.handleUIActions(UIAction.RetryLoadMoreAction)
        }
        tvError.setOnClickListener {
            repositoriesViewModel.handleUIActions(UIAction.RetryAction)
        }
        initSwipeRefreshLayout()
        initRecyclerView()
        repositoriesViewModel.observeOnUIState(this, Observer {
            when (it) {
                is UIState.ShowLoading -> showLoading()

                is UIState.HideLoading -> hideLoading()

                is UIState.ShowLoadingMore -> showLoadingMore()

                is UIState.HideLoadingMore -> hideLoadingMore()

                is UIState.ShowData -> showData(it.repos)

                is UIState.NewData -> updateData(it.repos, it.from, it.to)

                is UIState.ShowError -> showError(it.message)

                is UIState.ShowLoadMoreError -> showLoadMoreError()

                is UIState.HideLoadMoreError -> hideLoadMoreError()

                is UIState.HideError -> hideError()

                is UIState.HideRefresh -> hideRefresh()

                else -> throw UnsupportedOperationException("This state $it is not supported or not implemented yet!")
            }
        })
    }

    private fun initSwipeRefreshLayout() {
        srlRepos.apply {
            setDistanceToTriggerSync(15)
            setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent)
            setOnRefreshListener {
                repositoriesViewModel.handleUIActions(UIAction.RefreshAction)
            }
        }
    }

    private fun initRecyclerView() {
        rvRepos.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = RepositoriesAdapter()
            setHasFixedSize(true)
        }
    }

    private fun showLoading() {
        progressContainer.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        progressContainer.visibility = View.GONE
    }

    private fun showLoadingMore() {
        loadingMoreContainer.visibility = View.VISIBLE
    }

    private fun hideLoadingMore() {
        loadingMoreContainer.visibility = View.GONE
    }

    private fun showData(repos: List<Repo>) {
        (rvRepos.adapter as RepositoriesAdapter).apply {
            setData(repos)
            notifyDataSetChanged()
        }
        hideLoading()
        hideError()
    }

    private fun updateData(repos: List<Repo>, from: Int, to: Int) {
        hideError()
        hideLoadingMore()
        (rvRepos.adapter as RepositoriesAdapter).apply {
            setData(repos)
            notifyItemRangeChanged(from, to - from)
        }
    }

    private fun showError(message: String) {
        tvError.text = message
        errorContainer.visibility = View.VISIBLE
        hideLoading()
    }

    private fun showLoadMoreError() {
        hideLoadingMore()
        tvLoadMoreError.visibility = View.VISIBLE
    }

    private fun hideLoadMoreError() {
        tvLoadMoreError.visibility = View.GONE
    }

    private fun hideError() {
        errorContainer.visibility = View.GONE
    }

    private fun hideRefresh() {
        srlRepos.apply {
            if (isRefreshing) {
                isRefreshing = false
            }
        }
    }

    inner class RepositoriesAdapter : RecyclerView.Adapter<RepositoryViewHolder>() {

        private val repositories = ArrayList<Repo>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryViewHolder =
            RepositoryViewHolder(inflateView(R.layout.item_repository, parent))


        override fun getItemCount(): Int = repositories.size

        override fun onBindViewHolder(holder: RepositoryViewHolder, position: Int) {
            holder.onBind(repositories[position])
            if (
                itemCount - position == 1 &&
                tvLoadMoreError.visibility == View.GONE &&
                loadingMoreContainer.visibility == View.GONE
            ) {
                repositoriesViewModel.handleUIActions(UIAction.LoadMoreAction)
            }
        }

        @Suppress("SameParameterValue")
        private fun inflateView(@LayoutRes id: Int, parent: ViewGroup): View =
            LayoutInflater.from(parent.context).inflate(id, parent, false)

        fun setData(newRepos: List<Repo>) {
            repositories.clear()
            repositories.addAll(newRepos)
        }
    }

    inner class RepositoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(item: Repo) {
            itemView.apply {
                tvRepoName.text = item.name
                tvRepoDescription.apply {
                    if (item.description.isNullOrEmpty().not()) {
                        text = item.description
                        visibility = View.VISIBLE
                    } else {
                        visibility = View.GONE
                    }
                }
                tvRepoStars.text = item.stars.toString()
                tvRepoForks.text = item.forks.toString()
                tvRepoLanguage.text = item.language
                tvRepoSize.text = item.getSizeAndType()
            }
        }
    }
}