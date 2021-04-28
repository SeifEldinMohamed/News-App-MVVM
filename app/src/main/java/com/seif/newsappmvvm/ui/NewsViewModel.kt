package com.seif.newsappmvvm.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seif.newsappmvvm.NewsApplication
import com.seif.newsappmvvm.models.Article
import com.seif.newsappmvvm.models.NewsResponse
import com.seif.newsappmvvm.repository.NewsRepository
import com.seif.newsappmvvm.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import java.lang.Appendable

class NewsViewModel(
        val newsRepository: NewsRepository,
        app: Application

) : AndroidViewModel(app) {
    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse : NewsResponse? = null

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse : NewsResponse? = null

    var newSearchQuery:String? = null
    var oldSearchQuery:String? = null


    init {
        getBreakingNews("us")
        // put "eg" for egypt news
    }

    // the viewModelScope will keep the coroutine alive as long as this viewModel alive.
    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        // before network call we need to emit the loading state to our live data bec
        // we know that we are about to make the network call then loading state, So our fragment can handle that.
        safeBreakingNewsCall(countryCode)


    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        safeSearchNewsCall(searchQuery)
    }

    // in this fun we will decide whether we want to emit the success state in our breaking news live data
   // or the error state
    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {

        if (response.isSuccessful) {
            // check if response not equal to null, if that happened ...
            response.body()?.let { resultResponse ->
                // get the second page of news articles
                breakingNewsPage++
                if(breakingNewsResponse == null){
                    breakingNewsResponse = resultResponse
                }else{
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                // if the breakingNewsResponse is equal to null we will return the result response
                return Resource.Success(breakingNewsResponse?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if(response.isSuccessful) {
        // check if response not equal to null, if that happened ...

            response.body()?.let { resultResponse ->
                // get the second page of news articles
                if(searchNewsResponse == null || newSearchQuery != oldSearchQuery) {
                    searchNewsPage = 1
                    oldSearchQuery = newSearchQuery
                    searchNewsResponse = resultResponse
                } else {
                    searchNewsPage++
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                // if the breakingNewsResponse is equal to null we will return the result response
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }

        return Resource.Error(response.message())
    }

     fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }
     fun getSavedNews() = newsRepository.getSavedNews()

     fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }


    private suspend fun safeBreakingNewsCall(countryCode: String){
        breakingNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
                breakingNews.postValue(handleBreakingNewsResponse(response))
            }else{
                breakingNews.postValue(Resource.Error("No internet connection"))
            }

        }
        catch (t :Throwable){
            when(t){
                // that could happened from retrofit
               is IOException -> breakingNews.postValue(Resource.Error("Network Failure"))
                else -> breakingNews.postValue(Resource.Error("Conversion Error"))
            }

        }
    }
    private suspend fun safeSearchNewsCall(searchQuery: String){
        newSearchQuery = searchQuery

        searchNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = newsRepository.searchNews(searchQuery, searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            }else{
                searchNews.postValue(Resource.Error("No internet connection"))
            }

        }
        catch (t :Throwable){
            when(t){
                // that could happened from retrofit
                is IOException -> searchNews.postValue(Resource.Error("Network Failure"))
                else -> searchNews.postValue(Resource.Error("Conversion Error"))
            }

        }
    }

    // check if the user is connected to the internet.
    private fun hasInternetConnection(): Boolean{
        // the getApplication fun is only available in the android view model
        // this connectivityManager will just used to detect if the user in currently connected to the internet or not.
        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            val activeNetwork = connectivityManager.activeNetwork?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)?: return false
            return when{
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }
        else{ // for api less than 23
            connectivityManager.activeNetworkInfo?.run {
                return when(type){
                    TYPE_WIFI -> true
                    TYPE_MOBILE ->true
                    TYPE_ETHERNET ->true
                    else -> false
                }
            }
        }
        return false

    }
    // connectivity manager is a system service that carry the context
}

