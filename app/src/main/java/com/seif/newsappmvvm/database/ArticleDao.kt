package com.seif.newsappmvvm.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.seif.newsappmvvm.models.Article

// to let room know that this is the interface that defines the functions for us.
@Dao
interface ArticleDao {
    // function for insert or update an article.
    // on conflict strategy determine what happens if that article that we want to insert is already exist in the database.
    // in that case we want to replace that article
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    // it will return the id that was inserted.
    suspend fun upsert(article : Article):Long

    // we will pass a sql query that should select all the articles this function should return.
    @Query("Select * From articles")
    fun getAllArticles():LiveData<List<Article>>
    // we create a function to delete an Article.
    @Delete
    suspend fun deleteArticle(article: Article)
}