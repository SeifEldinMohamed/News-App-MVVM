package com.seif.newsappmvvm.adapters

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.seif.newsappmvvm.R
import com.seif.newsappmvvm.models.Article
import com.seif.newsappmvvm.ui.fragments.ArticleFragment
import kotlinx.android.synthetic.main.item_article_preview.view.*

class NewsAdapter: RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {
    // we will make a callback for our async list differ so the async list will be the tool
    // that compares our two lists and updates only the item that changes.
    private val differCallback = object : DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)

    inner class ArticleViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
                LayoutInflater.from(parent.context).inflate(
                        R.layout.item_article_preview,
                        parent,
                        false
                )
        )

    }

    override fun getItemCount(): Int {
        return differ.currentList.size

    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage).into(ivArticleImage)
            tvSource.text = article.source?.name
            tvTitle.text = article.title
            tvDescription.text = article.description
            tvPublishedAt.text = article.publishedAt
            setOnClickListener {
                onItemClickListener?.let { it(article) }
//                val intent = Intent(Intent.ACTION_VIEW,
//                    Uri.parse(differ.currentList[position].url))
//                holder.itemView.context.startActivity(intent)

           }
        }
    }
    private var onItemClickListener:((Article) -> Unit)? = null
    fun setOnItemClickListener(listener :(Article) -> Unit){
        onItemClickListener = listener
    }



    // we will diffUtil calc the diff bet 2 lists and enables us to only update the items that are different
}   // and also it works in the background so we won't block our main thread with that.