package com.seif.newsappmvvm.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

// This article is a table in our database
@Entity(
        tableName = "articles"
)
data class Article(

        // We made the primary key of the database "id" and it is a nullable integer bec not every article will have an id
        // Due to the fact that we get a lot of articles from retrofit that we don't save in the database.
        @PrimaryKey(autoGenerate = true)
        var id: Int? = null,
        // Columns of the table.
        // While each row will represent a single entry in the database.
        val author: String?,
        val content: String?,
        val description: String?,
        val publishedAt: String?,
        val source: Source?,
        val title: String?,
        val url: String?,
        val urlToImage: String?
): Serializable
// DAO(Data Access Object): It's an interface that will define
// the functions that access our local database(save, read ,delete, etc..).
