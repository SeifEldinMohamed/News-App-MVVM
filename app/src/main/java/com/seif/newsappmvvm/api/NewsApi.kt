package com.seif.newsappmvvm.api

import com.seif.newsappmvvm.models.NewsResponse
import com.seif.newsappmvvm.utils.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// this interface will use to define our single requests so we can execute it from code.
interface NewsApi {
    @GET("v2/top-headlines")
    // create our fun that gets the breaking news bec this is a network call fun we want to execute this
    // fun asynchronously by using coroutines
    suspend fun getBreakingNews(
        // to specify from which country you get the breaking news we have to add parameters to this fun
        // if that is a request parameter then we need to annotate that parameter with query
        @Query("country")
        countryCode: String = "us",
        @Query("page") // to help us in pagination
        pageNumber: Int = 1 ,
        @Query("apiKey")
        apiKey: String = API_KEY
    ):Response<NewsResponse>

    // for search in news
    @GET("v2/everything")
    suspend fun searchForNews(
        @Query("q")
        searchQuery: String,
        @Query("page")
        pageNumber: Int = 1 ,
        @Query("apiKey")
        apiKey: String = API_KEY
    ):Response<NewsResponse>
}