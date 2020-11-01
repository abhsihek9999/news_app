package com.android.newsapp.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.newsapp.data.NewsAppRepository

class HomeViewModelFactory (
    private val app:Application,
    private val newsAppRepository: NewsAppRepository
): ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(app,newsAppRepository) as T
    }
}