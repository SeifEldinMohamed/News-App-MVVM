package com.seif.newsappmvvm.repository

import com.seif.newsappmvvm.api.RetrofitInstance
import com.seif.newsappmvvm.database.ArticleDataBase
import com.seif.newsappmvvm.models.Article
import retrofit2.Retrofit

// the purpose of this repository is to get the data from database and our remote data source from retrofit(our api).
class NewsRepository(
        // we will take the local database bec we will need that database to access the fun of our database
        val db: ArticleDataBase
) {
    suspend fun getBreakingNews(countrycode: String, pageNumber: Int) =
            RetrofitInstance.api.getBreakingNews(countrycode, pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
            RetrofitInstance.api.searchForNews(searchQuery, pageNumber)

    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

    // return live data.
    fun getSavedNews() = db.getArticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)



}