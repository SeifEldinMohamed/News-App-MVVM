package com.seif.newsappmvvm.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.seif.newsappmvvm.repository.NewsRepository

// we need to make viewModelProviderFactory to define how our own view model should be created
class NewsViewModelProviderFactory(
        val app: Application,
        val newsRepository: NewsRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewsViewModel(newsRepository, app) as T
    }

}