package com.android.newsapp.data

import com.android.newsapp.data.db.NewsDatabase
import com.android.newsapp.data.db.entity.Article
import com.android.newsapp.data.network.NewsAppApi
import com.android.newsapp.data.network.RetrofitInstance

class NewsAppRepository(
    private val database: NewsDatabase
) {

      suspend  fun getNewsFromApi(
            countryCode:String,
            page:Int
        ) = RetrofitInstance.api.getBreakingNews(countryCode,page)

     suspend fun getSearchNewsFromApi(
        countryCode:String,
        page:Int
    ) = RetrofitInstance.api.search(countryCode,page)


    suspend fun upsert(article:Article) = database.newsDao().upsert(article)

    fun getSavedArticles() = database.newsDao().getArticles()

    suspend fun deleteArticle(article:Article) = database.newsDao().deleteArticle(article)
}