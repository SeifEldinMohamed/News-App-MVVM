package com.seif.newsappmvvm.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seif.newsappmvvm.R
import com.seif.newsappmvvm.adapters.NewsAdapter
import com.seif.newsappmvvm.ui.NewsActivity
import com.seif.newsappmvvm.ui.NewsViewModel
import com.seif.newsappmvvm.utils.Constants.Companion.QUERY_PAGE_SIZE
import com.seif.newsappmvvm.utils.Resource
import kotlinx.android.synthetic.main.fragment_breaking_news.*

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    val TAG = "BreakingNewsFragment"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // we have access to the viewModel created in news activity
        viewModel = (activity as NewsActivity).viewModel
        setUpRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle()
            bundle.putSerializable("article", it)
            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle
            )
        }

        // we basically subscribe to all the changes regarding their life data.
        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    // check if data not equal to null
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        // we need to set up our isLessPage ruling here bec every time we get a news response then
                        // it could be our last page and we need to notify our scroll listener about that.(paginate further or not)
                        // we add 2 bec we have an integer division that is
                        // rounded off and 1 for the last page of our response that will be empty.
                        val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.breakingNewsPage == totalPages

                        if (isLastPage) {
                            rvBreakingNews.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    // check if it's not equal to null
                    response.message?.let { message ->
                        Toast.makeText(activity, "An Error occurred: $message", Toast.LENGTH_SHORT).show()

                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })

        swipeRefresh.setOnRefreshListener {
            viewModel.getBreakingNews("us")
            swipeRefresh.isRefreshing = false
        }

    }

    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false

    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    // if we loading a new page.
    var isLoading = false

    // to determine if we should stop paginating
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            // check if we are currently scrolling.
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            // sadly there is not a mechanism to know whether if we scroll until the bottom or not
            // so we need to make some calculations with the layout manager of our recycler view.
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstItemVisiblePosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItemVisible = firstItemVisiblePosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstItemVisiblePosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate =
                isNotLoadingAndNotLastPage && isAtLastItemVisible && isNotAtBeginning
                        && isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                viewModel.getBreakingNews("us")
                isScrolling = false
            }

        }
    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter()
        rvBreakingNews.layoutManager = LinearLayoutManager(activity)
        rvBreakingNews.adapter = newsAdapter
        rvBreakingNews.addOnScrollListener(this@BreakingNewsFragment.scrollListener)
    }
}