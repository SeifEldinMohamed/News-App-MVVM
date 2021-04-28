package com.seif.newsappmvvm.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.seif.newsappmvvm.R
import com.seif.newsappmvvm.adapters.NewsAdapter
import com.seif.newsappmvvm.ui.NewsActivity
import com.seif.newsappmvvm.ui.NewsViewModel
import kotlinx.android.synthetic.main.fragment_article.*
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.android.synthetic.main.fragment_saved_news.*
import kotlinx.android.synthetic.main.item_article_preview.*

class SavedNewsFragment:Fragment(R.layout.fragment_saved_news) {
    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        setUpRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle()
            bundle.putSerializable("article", it)
            findNavController().navigate(
                R.id.action_savedNewsFragment_to_articleFragment,
                bundle
            )
        }
        // to be able to swipe to delete article we need to add item touch helper to our recycler view adapter
        val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(
            // direction in which we want to drag our recycler view itself.
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            // the directions we want to be able to swipe our items for the directors.
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                // article we want to delete.
                val article = newsAdapter.differ.currentList[position]
                viewModel.deleteArticle(article)
                // we add the functionality to undo the article deleted
                Snackbar.make(view, "Article deleted successfully",Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){
                        viewModel.saveArticle(article)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallBack).apply {
            attachToRecyclerView(rvSavedNews)
        }

        viewModel.getSavedNews().observe(viewLifecycleOwner, Observer { article ->
            newsAdapter.differ.submitList(article)
        })


    }
    private fun setUpRecyclerView(){
        newsAdapter = NewsAdapter()
        rvSavedNews.layoutManager = LinearLayoutManager(activity)
        rvSavedNews.adapter = newsAdapter
    }
}