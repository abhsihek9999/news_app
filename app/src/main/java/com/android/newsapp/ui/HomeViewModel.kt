package com.android.newsapp.ui

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
import com.android.NewsApplication
import com.android.newsapp.data.NewsAppRepository
import com.android.newsapp.data.db.entity.Article
import com.android.newsapp.data.network.response.BreakingNewsResponse
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class HomeViewModel(
    private val app:Application,
    private val newsAppRepository: NewsAppRepository
) : AndroidViewModel(app) {

    val newsLiveData : MutableLiveData<Resource<BreakingNewsResponse>> = MutableLiveData()
    var page = 1
    var breakingNewsResponse : BreakingNewsResponse? = null

    val searchLiveData : MutableLiveData<Resource<BreakingNewsResponse>> = MutableLiveData()
    var searchPage = 1
    var searchNewsResponse : BreakingNewsResponse? = null

    init {
        getBreakingNews("US")
    }

    fun getBreakingNews(countryCode:String) =
       safeBreakingNewsCall(countryCode)



    fun getSearchNews(query:String) =
       safeSearchNewsCall(query)


    private fun handleResponse(response: Response<BreakingNewsResponse>):Resource<BreakingNewsResponse>{

        if(response.isSuccessful){

            response.body()?.let { newsResponse->
                page++
                if(breakingNewsResponse == null){
                breakingNewsResponse = newsResponse
                }else{
                    val oldArticle = breakingNewsResponse?.articles
                    val newsArticle = newsResponse.articles
                    oldArticle?.addAll(newsResponse.articles)
                }

                return Resource.Success(breakingNewsResponse?:newsResponse)
            }

        }

        return Resource.Error(response.message())

    }


    private fun handleSearchResponse(response: Response<BreakingNewsResponse>):Resource<BreakingNewsResponse>{

        if(response.isSuccessful){

            response.body()?.let { newsResponse->
                searchPage ++
                if(searchNewsResponse == null){
                    searchNewsResponse = newsResponse

                }else{

                    val oldArticle = searchNewsResponse?.articles
                    val newsArticle = newsResponse.articles
                    oldArticle?.addAll(newsArticle)
                }

                return Resource.Success(searchNewsResponse?:newsResponse)
            }

        }

        return Resource.Error(response.message())

    }


    fun upsert(article:Article) = viewModelScope.launch {
        newsAppRepository.upsert(article)
    }

    fun getSavedArticles() = newsAppRepository.getSavedArticles()


    fun deleteArticles(article: Article) = viewModelScope.launch {
        newsAppRepository.deleteArticle(article)
    }

    fun safeBreakingNewsCall(countryCode: String){
        newsLiveData.postValue(Resource.Loading())

        try {

            if(handleInternetConnection()){
            viewModelScope.launch {
                newsLiveData.postValue(Resource.Loading())
                val response = newsAppRepository.getNewsFromApi(countryCode, page)

                newsLiveData.postValue(handleResponse(response))
            }
            }else{
                newsLiveData.postValue(Resource.Error("No internet connectivity"))
            }
        }catch (t:Throwable){

            when(t){
                is IOException -> newsLiveData.postValue(Resource.Error("Network Failure"))
                else ->
                    newsLiveData.postValue(Resource.Error("Conversion error"))
            }

        }

    }

    fun safeSearchNewsCall(query: String){
        searchLiveData.postValue(Resource.Loading())

        try {

            if(handleInternetConnection()){
                viewModelScope.launch {
                    searchLiveData.postValue(Resource.Loading())
                    val response = newsAppRepository.getSearchNewsFromApi(query, searchPage)

                    searchLiveData.postValue(handleSearchResponse(response))
                }
            }else{
                searchLiveData.postValue(Resource.Error("No internet connectivity"))
            }
        }catch (t:Throwable){

            when(t){
                is IOException -> searchLiveData.postValue(Resource.Error("Network Failure"))
                else ->
                    searchLiveData.postValue(Resource.Error("Conversion error"))
            }

        }

    }

    fun handleInternetConnection():Boolean {

        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activityNetwork = connectivityManager.activeNetwork ?: return false

            val capabilities = connectivityManager.getNetworkCapabilities(activityNetwork)

          return  when{

                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else ->
                    false

            }
        }else{

            connectivityManager.activeNetworkInfo?.run {
                return when(type){
                    TYPE_WIFI -> true
                        TYPE_MOBILE -> true
                        TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

}