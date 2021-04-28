package com.seif.newsappmvvm.models

import com.seif.newsappmvvm.models.Article

data class NewsResponse(
        val articles: MutableList<Article>,
        val status: String,
        val totalResults: Int
)